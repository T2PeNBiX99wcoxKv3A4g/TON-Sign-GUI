package io.github.t2penbix99wcoxkv3a4g.tonsign

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.Notification
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.RoundData
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.interpreter.LogLevel
import io.github.t2penbix99wcoxkv3a4g.tonsign.interpreter.LogLevel.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.SaveManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.TimerManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.WonOrLost
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.nextPrediction
import java.time.format.DateTimeFormatter

object EventHandle {
    internal const val ROUND_TIMER_ID = "RoundTimer"

    private val queries = SaveManager.database.saveQueries

    internal val logs = mutableStateListOf<AnnotatedString>()
    internal val players = mutableStateListOf<PlayerData>()
    internal val nowWorldID = mutableStateOf("")
    internal var isInWorld = mutableStateOf(false)
    internal var lastTime = queries.selectLast().executeAsOneOrNull()?.MAX ?: 0L

    private var isRoundStart = false
    private var isTimerSet = false
    private var roundSkip = false
    
    init {
        EventBus.register(this)
    }

    @Subscribe("OnExit")
    private fun onExit(event: OnExitEvent) {
        nextPrediction.value = GuessRoundType.NIL

        val roundData = queries.selectOfTime(lastTime).executeAsOneOrNull() ?: return
        if (roundData.isWon == WonOrLost.InProgress)
            queries.updateIsWon(WonOrLost.UnKnown, lastTime)
        queries.updateRoundTime(TimerManager.get(ROUND_TIMER_ID), lastTime)
        queries.updatePlayerTime(TimerManager.get(ROUND_TIMER_ID), lastTime)
    }

    @Subscribe("OnNextPrediction")
    private fun onNextPrediction(event: OnNextPredictionEvent) {
        val guessRound = event.nextPrediction
        nextPrediction.value = guessRound
    }

    @Subscribe("OnJoinRoom")
    private fun onJoinRoom(event: OnJoinRoomEvent) {
        val worldId = event.worldId
        isInWorld.value = true
        nowWorldID.value = worldId
    }

    @Subscribe("OnLeftTON")
    private fun onLeftTON(event: OnLeftTONEvent) {
        nextPrediction.value = GuessRoundType.NIL

        val roundData = queries.selectOfTime(lastTime).executeAsOneOrNull() ?: return
        if (roundData.isWon == WonOrLost.InProgress)
            queries.updateIsWon(WonOrLost.UnKnown, lastTime)
        queries.updateRoundTime(TimerManager.get(ROUND_TIMER_ID), lastTime)
        queries.updatePlayerTime(TimerManager.get(ROUND_TIMER_ID), lastTime)
    }

    @Subscribe("OnLeftRoom")
    private fun onLeftRoom(event: OnLeftRoomEvent) {
        players.clear()
        isInWorld.value = false
        nowWorldID.value = ""
        isRoundStart = false
        isTimerSet = false
    }

    @Subscribe("OnRoundStart")
    private fun onRoundStart(event: OnRoundStartEvent) {
        val time = event.time
        val round = event.roundType
        val map = event.map
        val mapId = event.mapId
        val roundData = queries.selectOfTime(lastTime).executeAsOneOrNull()

        if (roundData != null && roundData.isWon == WonOrLost.InProgress)
            queries.updateIsWon(WonOrLost.UnKnown, lastTime)

        val logTime = time.toInstant().epochSecond
        val newRoundData = queries.selectOfTime(logTime).executeAsOneOrNull()

        if (newRoundData == null)
            queries.insert(
                RoundData(
                    time = logTime,
                    roundType = round,
                    map = map,
                    mapId = mapId,
                    roundTime = -1L,
                    playerTime = -1L,
                    players = players.toMutableList(),
                    terrors = arrayListOf(-1, -1, -1),
                    isDeath = false,
                    isWon = WonOrLost.InProgress
                )
            )
        else
            roundSkip = true

        lastTime = time.toInstant().epochSecond
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

        val roundData = queries.selectOfTime(lastTime).executeAsOneOrNull() ?: return
        if (roundData.isWon == WonOrLost.InProgress)
            queries.updateIsWon(WonOrLost.UnKnown, lastTime)
        queries.updateRoundTime(TimerManager.get(ROUND_TIMER_ID), lastTime)
    }

    @Subscribe("OnRoundOver")
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

    @Subscribe("OnPlayerJoinedRoom")
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

    @Subscribe("OnPlayerLeftRoom")
    private fun onPlayerLeftRoom(event: OnPlayerLeftRoomEvent) {
        val player = event.playerData
        val isInWorld by isInWorld

        players.removeAll { it.id == player.id }
        sendNotificationOnPlayerLeftRoom(player)
        
        if (!isRoundStart) return

        val lastTime = lastTime
        val roundData = queries.selectOfTime(lastTime).executeAsOneOrNull()
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
        queries.updatePlayers(players, lastTime)
    }

    @Subscribe("OnPlayerDeath")
    private fun onPlayerDeath(event: OnPlayerDeathEvent) {
        var player = event.playerData
        val lastTime = lastTime
        val roundData = queries.selectOfTime(lastTime).executeAsOneOrNull()
        if (roundData == null || roundSkip) return
        val players = roundData.players
        val data = players.find { it.name == player.name }
        if (data == null) return
        val i = players.indexOf(data)

        if (player.id.isNullOrBlank())
            player = player.copy(id = data.id)
        players[i] = player
        queries.updatePlayers(players, lastTime)
    }

    @Subscribe("OnRoundWon")
    private fun onRoundWon(event: OnRoundWonEvent) {
        val lastTime = lastTime
        val roundData = queries.selectOfTime(lastTime).executeAsOneOrNull()
        if (roundData == null || roundSkip) return
        queries.updateIsWon(WonOrLost.Won, lastTime)
    }

    @Subscribe("OnRoundLost")
    private fun onRoundLost(event: OnRoundLostEvent) {
        val lastTime = lastTime
        val roundData = queries.selectOfTime(lastTime).executeAsOneOrNull()
        if (roundData == null || roundSkip) return
        queries.updateIsWon(WonOrLost.Lost, lastTime)
    }

    @Subscribe("OnRoundDeath")
    private fun onRoundDeath(event: OnRoundDeathEvent) {
        val lastTime = lastTime
        val roundData = queries.selectOfTime(lastTime).executeAsOneOrNull()
        if (roundData == null || roundSkip) return
        queries.updatePlayerTime(TimerManager.get(ROUND_TIMER_ID), lastTime)
        queries.updateIsDeath(true, lastTime)
    }

    @Subscribe("OnKillerSet")
    private fun onKillerSet(event: OnKillerSetEvent) {
        val terrors = event.killerMatrix
        if (!isTimerSet && terrors[0] > -1) {
            TimerManager.set(ROUND_TIMER_ID)
            isTimerSet = true
        }
        val lastTime = lastTime
        val roundData = queries.selectOfTime(lastTime).executeAsOneOrNull()
        if (roundData == null || roundSkip) return
        queries.updateTerrors(terrors, lastTime)
    }

    @Subscribe("OnHideTerrorShowUp")
    private fun onHideTerrorShowUp(event: OnHideTerrorShowUpEvent) {
        val terrorId = event.newTerrorId
        val lastTime = lastTime
        val roundData = queries.selectOfTime(lastTime).executeAsOneOrNull()
        if (roundData == null || roundSkip) return
        val terrors = roundData.terrors
        terrors[0] = terrorId
        queries.updateTerrors(terrors, lastTime)
    }

    private fun getLogLevelColor(level: LogLevel): Color {
        return when (level) {
            ERROR, EXCEPTION -> Color.Red
            WARN -> Color.Yellow
            DEBUG -> Color.Green
            else -> Color.White
        }
    }

    @Subscribe("OnReadLog")
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