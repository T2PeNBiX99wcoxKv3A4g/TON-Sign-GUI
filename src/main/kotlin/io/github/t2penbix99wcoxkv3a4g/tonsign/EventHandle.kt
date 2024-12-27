package io.github.t2penbix99wcoxkv3a4g.tonsign

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.Notification
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.RoundData
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.interpreter.LogLevel
import io.github.t2penbix99wcoxkv3a4g.tonsign.interpreter.LogLevel.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.SaveManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.TimerManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.Terror
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.WonOrLost
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.nextPrediction
import java.time.format.DateTimeFormatter

object EventHandle {
    internal const val ROUND_TIMER_ID = "RoundTimer"

    private val queries = SaveManager.database.saveQueries

    internal val logs = mutableStateListOf<AnnotatedString>()
    internal val players = mutableStateListOf<PlayerData>()
    internal val nowWorldID = mutableStateOf("")
    internal val isInWorld = mutableStateOf(false)
    internal val currentTime by lazy { mutableStateOf(queries.last() ?: 0L) }

    private var isRoundStart = false
    private var isTimerSet = false
    private var roundSkip = false
    private var tempTerrors: ArrayList<Int>? = null

    init {
        EventBus.register(this)
    }

    private fun exitDo() {
        nextPrediction.value = GuessRoundType.NIL
        val currentTime by currentTime
        val roundData = queries.timeOf(currentTime) ?: return
        if (roundData.isWon == WonOrLost.InProgress)
            queries.setIsWon(currentTime, WonOrLost.UnKnown)
        queries.setRoundTime(currentTime, TimerManager.get(ROUND_TIMER_ID))
        queries.setPlayerTime(currentTime, TimerManager.get(ROUND_TIMER_ID))
    }

    @Subscribe(Events.OnExit)
    private fun onExit(event: OnExitEvent) {
        exitDo()
    }

    @Subscribe(Events.OnVRChatQuit)
    private fun onVRChatQuit(event: OnVRChatQuitEvent) {
        exitDo()
    }

    @Subscribe(Events.OnNextPrediction)
    private fun onNextPrediction(event: OnNextPredictionEvent) {
        val guessRound = event.nextPrediction
        nextPrediction.value = guessRound
    }

    @Subscribe(Events.OnJoinRoom)
    private fun onJoinRoom(event: OnJoinRoomEvent) {
        val worldId = event.worldId
        isInWorld.value = true
        nowWorldID.value = worldId
    }

    @Subscribe(Events.OnLeftTON)
    private fun onLeftTON(event: OnLeftTONEvent) {
        exitDo()
    }

    @Subscribe(Events.OnLeftRoom)
    private fun onLeftRoom(event: OnLeftRoomEvent) {
        players.clear()
        isInWorld.value = false
        nowWorldID.value = ""
        isRoundStart = false
        isTimerSet = false
    }

    @Subscribe(Events.OnRoundStart)
    private fun onRoundStart(event: OnRoundStartEvent) {
        val time = event.time
        val round = event.roundType
        val map = event.map
        val mapId = event.mapId
        var currentTime by currentTime
        val lastRoundData = queries.timeOf(currentTime)

        if (lastRoundData != null && lastRoundData.isWon == WonOrLost.InProgress)
            queries.setIsWon(currentTime, WonOrLost.UnKnown)
        currentTime = time.toInstant().epochSecond
        val newRoundData = queries.timeOf(currentTime)
        var newTerrors = tempTerrors ?: arrayListOf(-1, -1, -1)
        
        // For some reason not unknown id
        if (round == RoundType.Fog)
            newTerrors = arrayListOf(Terror.UNKNOWN,Terror.UNKNOWN,Terror.UNKNOWN)

        if (newRoundData == null)
            queries.add(
                RoundData(
                    time = currentTime,
                    roundType = round,
                    map = map,
                    mapId = mapId,
                    roundTime = -1L,
                    playerTime = -1L,
                    players = players.toMutableList(),
                    terrors = newTerrors,
                    isDeath = false,
                    isWon = WonOrLost.InProgress
                )
            )
        else
            roundSkip = true
        tempTerrors = null
        isRoundStart = true
    }

    private fun sendNotificationOnRoundOver(guessRound: GuessRoundType) {
        if (roundSkip || !ConfigManager.config.roundNotify) return
        if (guessRound == GuessRoundType.NIL || guessRound == GuessRoundType.Exempt) return
        if (ConfigManager.config.roundNotifyOnlySpecial && guessRound != GuessRoundType.Special) return

        val special = "gui.text.main.round_special".i18n()
        val classic = "gui.text.main.round_classic".i18n()

        val nextPredictionNotification = Notification(
            "gui.notification.next_prediction_title".i18n(),
            "gui.notification.next_prediction_message".i18n(if (guessRound == GuessRoundType.Special) special else classic)
        )

        trayState.sendNotification(nextPredictionNotification)
    }

    private fun setRoundDataOnRoundOver() {
        if (!isRoundStart) return
        isRoundStart = false

        val currentTime by currentTime
        val roundData = queries.timeOf(currentTime) ?: return
        if (roundData.isWon == WonOrLost.InProgress)
            queries.setIsWon(currentTime, WonOrLost.UnKnown)
        queries.setRoundTime(currentTime, TimerManager.get(ROUND_TIMER_ID))
    }

    @Subscribe(Events.OnRoundOver)
    private fun onRoundOver(event: OnRoundOverEvent) {
        val guessRound = event.lastPrediction
        sendNotificationOnRoundOver(guessRound)
        setRoundDataOnRoundOver()
        roundSkip = false
        isTimerSet = false
    }

    private fun sendNotificationOnPlayerJoinedRoom(player: PlayerData) {
        if (!isInWorld.value || roundSkip || !ConfigManager.config.playerJoinedNotify) return

        val newPlayerNotification = Notification(
            "gui.notification.player_join_world_title".i18n(),
            "gui.notification.player_join_world_message".i18n(player.name)
        )

        trayState.sendNotification(newPlayerNotification)
    }

    @Subscribe(Events.OnPlayerJoinedRoom)
    private fun onPlayerJoinedRoom(event: OnPlayerJoinedRoomEvent) {
        val player = event.playerData
        players.add(player)
        sendNotificationOnPlayerJoinedRoom(player)
    }

    private fun sendNotificationOnPlayerLeftRoom(player: PlayerData) {
        if (!isInWorld.value || roundSkip || !ConfigManager.config.playerLeftNotify) return

        val playerLeftNotification = Notification(
            "gui.notification.player_left_world_title".i18n(),
            "gui.notification.player_left_world_message".i18n(player.name)
        )

        trayState.sendNotification(playerLeftNotification)
    }

    @Subscribe(Events.OnPlayerLeftRoom)
    private fun onPlayerLeftRoom(event: OnPlayerLeftRoomEvent) {
        val player = event.playerData
        val isInWorld by isInWorld

        players.removeAll { it.id == player.id }
        sendNotificationOnPlayerLeftRoom(player)

        if (!isRoundStart) return

        val currentTime by currentTime
        val roundData = queries.timeOf(currentTime)
        if (roundData == null || roundSkip) return
        val players = roundData.players
        val data = players.find { it.id == player.id }
        if (data == null) return
        val i = players.indexOf(data)
        if (players[i].status == PlayerStatus.Death) return
        if (isInWorld)
            players[i].status = PlayerStatus.Left
        else
            players[i].status = PlayerStatus.Unknown
        queries.setPlayers(currentTime, players)
    }

    @Subscribe(Events.OnPlayerDeath)
    private fun onPlayerDeath(event: OnPlayerDeathEvent) {
        var player = event.playerData
        val currentTime by currentTime
        val roundData = queries.timeOf(currentTime)
        if (roundData == null || roundSkip) return
        val players = roundData.players
        val data = players.find { it.name == player.name }
        if (data == null) return
        val i = players.indexOf(data)

        if (player.id.isNullOrBlank())
            player = player.copy(id = data.id)
        players[i] = player
        queries.setPlayers(currentTime, players)
    }

    @Subscribe(Events.OnRoundWon)
    private fun onRoundWon(event: OnRoundWonEvent) {
        val currentTime by currentTime
        val roundData = queries.timeOf(currentTime)
        if (roundData == null || roundSkip) return
        queries.setIsWon(currentTime, WonOrLost.Won)
    }

    @Subscribe(Events.OnRoundLost)
    private fun onRoundLost(event: OnRoundLostEvent) {
        val currentTime by currentTime
        val roundData = queries.timeOf(currentTime)
        if (roundData == null || roundSkip) return
        queries.setIsWon(currentTime, WonOrLost.Lost)
    }

    @Subscribe(Events.OnRoundDeath)
    private fun onRoundDeath(event: OnRoundDeathEvent) {
        val currentTime by currentTime
        val roundData = queries.timeOf(currentTime)
        if (roundData == null || roundSkip) return
        queries.setPlayerTime(currentTime, TimerManager.get(ROUND_TIMER_ID))
        queries.setIsDeath(currentTime, true)
    }

    @Subscribe(Events.OnKillerSet)
    private fun onKillerSet(event: OnKillerSetEvent) {
        val terrors = event.killerMatrix
        val currentTime by currentTime
        if (!isTimerSet && terrors[0] > -1) {
            TimerManager.set(ROUND_TIMER_ID)
            isTimerSet = true
        }
        if (!isRoundStart) {
            tempTerrors = terrors
            return
        }
        val roundData = queries.timeOf(currentTime)
        if (roundData == null || roundSkip) return
        queries.setTerrors(currentTime, terrors)
    }

    @Subscribe(Events.OnHideTerrorShowUp)
    private fun onHideTerrorShowUp(event: OnHideTerrorShowUpEvent) {
        val terrorId = event.newTerrorId
        val lastTime by currentTime
        val roundData = queries.timeOf(lastTime)
        if (roundData == null || roundSkip) return
        val terrors = roundData.terrors
        terrors[0] = terrorId
        queries.setTerrors(lastTime, terrors)
    }

    private fun getLogLevelColor(level: LogLevel): Color {
        return when (level) {
            ERROR, EXCEPTION -> Color.Red
            WARN -> Color.Yellow
            DEBUG -> Color.Green
            else -> Color.White
        }
    }

    @Subscribe(Events.OnReadLog)
    private fun onReadLog(event: OnReadLogEvent) {
        val log = event.logEvent
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val time = log.time.format(formatter)

        logs.add(buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Black)) {
                append("[$time]")
            }

            append(' ')
            append('[')

            withStyle(style = SpanStyle(color = getLogLevelColor(log.level))) {
                append(log.level.name)
            }

            append(']')
            append(' ')

            withStyle(style = SpanStyle(color = Color.Magenta)) {
                append("(${log.name ?: "Untitled"})")
            }

            append(':')
            append(' ')

            withStyle(style = SpanStyle(color = getLogLevelColor(log.level))) {
                append(log.msg)
            }
        })
    }
}