package io.github.t2penbix99wcoxkv3a4g.tonsign

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.Notification
import io.github.t2penbix99wcoxkv3a4g.tonsign.interpreter.LogEvent
import io.github.t2penbix99wcoxkv3a4g.tonsign.interpreter.LogLevel
import io.github.t2penbix99wcoxkv3a4g.tonsign.interpreter.LogLevel.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.TimerManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.RoundData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.RoundDataDetail
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.WonOrLost
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.nextPrediction
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.onExitEvent
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal const val RoundTimerID = "RoundTimer"

internal val roundDatas = mutableStateMapOf<Long, RoundData>()
internal val logs = mutableStateListOf<AnnotatedString>()
internal val players = mutableStateListOf<PlayerData>()
internal val nowWorldID = mutableStateOf("")
internal var isInWorld = mutableStateOf(false)
internal var lastTime = 0L

private var isRoundStart = false
private var isTimerSet = false
private var roundSkip = false

private fun onExit() {
    nextPrediction.value = GuessRoundType.NIL
    if (lastTime !in roundDatas) return
    val roundData = roundDatas[lastTime]!!
    if (roundData.roundDetail.isWon == WonOrLost.InProgress)
        roundData.roundDetail = roundData.roundDetail.copy(isWon = WonOrLost.UnKnown)
    roundData.roundDetail = roundData.roundDetail.copy(time = TimerManager.get(RoundTimerID))
}

private fun onNextPrediction(guessRound: GuessRoundType) {
    nextPrediction.value = guessRound
}

private fun onJoinRoom(worldId: String) {
    isInWorld.value = true
    nowWorldID.value = worldId
}

private fun onLeftTON() {
    nextPrediction.value = GuessRoundType.NIL
    if (lastTime !in roundDatas) return
    val roundData = roundDatas[lastTime]!!
    if (roundData.roundDetail.isWon == WonOrLost.InProgress)
        roundData.roundDetail = roundData.roundDetail.copy(isWon = WonOrLost.Left)
    roundData.roundDetail = roundData.roundDetail.copy(time = TimerManager.get(RoundTimerID))
}

private fun onLeftRoom() {
    players.clear()
    isInWorld.value = false
    nowWorldID.value = ""
    isRoundStart = false
    isTimerSet = false
}

private fun onRoundStart(time: ZonedDateTime, round: RoundType, map: String, id: Int) {
    val detail = RoundDataDetail(
        map,
        id,
        -1L,
        players.toMutableList(),
        arrayListOf(-1, -1, -1),
        false,
        WonOrLost.InProgress
    )

    if (lastTime in roundDatas) {
        val roundData = roundDatas[lastTime]!!
        if (roundData.roundDetail.isWon == WonOrLost.InProgress)
            roundData.roundDetail = roundData.roundDetail.copy(isWon = WonOrLost.UnKnown)
    }

    if (time.toInstant().epochSecond !in roundDatas)
        roundDatas[time.toInstant().epochSecond] = RoundData(time.toInstant().epochSecond, round, detail)
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

    if (lastTime !in roundDatas) return
    if (roundSkip) return

    val roundData = roundDatas[lastTime]!!
    if (roundData.roundDetail.isWon == WonOrLost.InProgress)
        roundData.roundDetail = roundData.roundDetail.copy(isWon = WonOrLost.UnKnown)
    roundData.roundDetail = roundData.roundDetail.copy(time = TimerManager.get(RoundTimerID))
}

private fun onRoundOver(guessRound: GuessRoundType) {
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

private fun onPlayerJoinedRoom(player: PlayerData) {
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

private fun onPlayerLeftRoom(player: PlayerData) {
    players.removeAll { it.id == player.id }
    sendNotificationOnPlayerLeftRoom(player)

    if (lastTime !in roundDatas || roundSkip) return
    val roundData = roundDatas[lastTime]!!
    val players = roundData.roundDetail.players
    val data = players.find { it.id == player.id }
    if (data == null) return
    val i = players.indexOf(data)
    if (players[i].status == PlayerStatus.Death) return
    if (isInWorld.value)
        players[i].status = PlayerStatus.Left
    else
        players[i].status = PlayerStatus.Unknown
}

private fun onPlayerDeath(player: PlayerData) {
    if (lastTime !in roundDatas || roundSkip) return
    val roundData = roundDatas[lastTime]!!
    val players = roundData.roundDetail.players
    val data = players.find { it.name == player.name }
    if (data == null) return
    val i = players.indexOf(data)
    var player = player
    if (player.id.isNullOrBlank())
        player = player.copy(id = data.id)
    players[i] = player
}

private fun onRoundWon() {
    if (lastTime !in roundDatas || roundSkip) return
    val roundData = roundDatas[lastTime]!!
    roundData.roundDetail = roundData.roundDetail.copy(isWon = WonOrLost.Won)
}

private fun onRoundLost() {
    if (lastTime !in roundDatas || roundSkip) return
    val roundData = roundDatas[lastTime]!!
    roundData.roundDetail = roundData.roundDetail.copy(isWon = WonOrLost.Lost)
}

private fun onRoundDeath() {
    if (lastTime !in roundDatas || roundSkip) return
    val roundData = roundDatas[lastTime]!!
    roundData.roundDetail = roundData.roundDetail.copy(isDeath = true)
}

private fun onKillerSet(terrors: ArrayList<Int>) {
    if (!isTimerSet) {
        TimerManager.set(RoundTimerID)
        isTimerSet = true
    }
    if (lastTime !in roundDatas || roundSkip) return
    val roundData = roundDatas[lastTime]!!
    roundData.roundDetail = roundData.roundDetail.copy(terrors = terrors)
}

private fun onHideTerrorShowUp(terrorId: Int) {
    if (lastTime !in roundDatas || roundSkip) return
    val roundData = roundDatas[lastTime]!!
    roundData.roundDetail.terrors[0] = terrorId
}

private fun getLogLevelColor(level: LogLevel): Color {
    return when (level) {
        ERROR, EXCEPTION -> Color.Red
        WARN -> Color.Yellow
        DEBUG -> Color.Green
        else -> Color.White
    }
}

private fun onReadLog(log: LogEvent) {
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

private var init = false

internal fun handleEvent(logWatcher: LogWatcher) {
    logWatcher.onReadLogEvent += ::onReadLog
    logWatcher.onNextPredictionEvent += ::onNextPrediction
    logWatcher.onRoundStartEvent += ::onRoundStart
    logWatcher.onRoundOverEvent += ::onRoundOver
    logWatcher.onRoundWonEvent += ::onRoundWon
    logWatcher.onRoundLostEvent += ::onRoundLost
    logWatcher.onRoundDeathEvent += ::onRoundDeath
    logWatcher.onJoinRoomEvent += ::onJoinRoom
    logWatcher.onLeftTONEvent += ::onLeftTON
    logWatcher.onLeftRoomEvent += ::onLeftRoom
    logWatcher.onPlayerJoinedRoomEvent += ::onPlayerJoinedRoom
    logWatcher.onPlayerLeftRoomEvent += ::onPlayerLeftRoom
    logWatcher.onPlayerDeathEvent += ::onPlayerDeath
    logWatcher.onKillerSetEvent += ::onKillerSet
    logWatcher.onHideTerrorShowUpEvent += ::onHideTerrorShowUp

    if (init) return
    init = true
    onExitEvent += ::onExit
}