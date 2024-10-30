package io.github.t2penbix99wcoxkv3a4g.tonsign

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.Notification
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.middlePath
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.RoundData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.RoundDataDetail
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.Terror
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.Terrors
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.WonOrLost
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.nextPrediction
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher

internal val roundDatas = mutableStateMapOf<String, RoundData>()
internal val logs = mutableStateListOf<AnnotatedString>()
internal val players = mutableStateListOf<PlayerData>()

private var lastTime = ""
private var isInWorld = false
private var isRoundStart = false

private const val AND_THE_ROUND_TYPE_IS = " and the round type is "
private const val ROUND_MAP_LOCATION = "This round is taking place at "

private fun onNextPrediction(guessRound: GuessRoundType) {
    nextPrediction.value = guessRound
}

private fun onJoinRoom(worldId: String) {
    isInWorld = true
}

private fun onLeftTON() {
    nextPrediction.value = GuessRoundType.NIL
    if (lastTime !in roundDatas) return
    val roundData = roundDatas[lastTime]!!
    roundData.roundDetail = roundData.roundDetail.copy(isWon = WonOrLost.Left)
}

private fun onLeftRoom() {
    players.clear()
    isInWorld = false
    isRoundStart = false

    if (lastTime !in roundDatas) return
    val roundData = roundDatas[lastTime]!!
    if (roundData.roundDetail.isWon == WonOrLost.InProgress)
        roundData.roundDetail = roundData.roundDetail.copy(isWon = WonOrLost.UnKnown)
}

private fun onRoundStart(time: String, round: RoundType, line: String) {
    var mapString = line.middlePath(ROUND_MAP_LOCATION, AND_THE_ROUND_TYPE_IS).trim()
    val idAndMap = mapString.split('(')
    val id = idAndMap[1].filter { it.isDigit() }.trim()
    val map = idAndMap[0].trim()
    val detail = RoundDataDetail(
        map,
        id.toInt(),
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

    roundDatas[time] = RoundData(time, round, detail)
    lastTime = time
    isRoundStart = true
}

private fun onRoundOver(guessRound: GuessRoundType) {
    if (guessRound == GuessRoundType.NIL || guessRound == GuessRoundType.Exempt) return
    if (ConfigManager.config.onlySpecial && guessRound == GuessRoundType.Classic) return

    val special = LanguageManager.get("gui.text.main.round_special")
    val classic = LanguageManager.get("gui.text.main.round_classic")

    val nextPredictionNotification = Notification(
        LanguageManager.get("gui.notification.next_prediction_title"),
        LanguageManager.get(
            "gui.notification.next_prediction_message",
            if (guessRound == GuessRoundType.Special) special else classic
        )
    )

    trayState.sendNotification(nextPredictionNotification)
    isRoundStart = false

    if (lastTime !in roundDatas) return
    val roundData = roundDatas[lastTime]!!
    if (roundData.roundDetail.isWon == WonOrLost.InProgress)
        roundData.roundDetail = roundData.roundDetail.copy(isWon = WonOrLost.UnKnown)
}

private fun onPlayerJoinedRoom(player: String) {
    players.add(PlayerData(player, PlayerStatus.Alive, null))
}

private fun onPlayerLeftRoom(player: String) {
    players.removeAll { it.name == player }
    if (lastTime !in roundDatas) return
    val roundData = roundDatas[lastTime]!!
    val players = roundData.roundDetail.players
    val data = players.find { it.name == player }
    if (data == null) return
    val i = players.indexOf(data)
    if (players[i].status == PlayerStatus.Death) return
    if (isInWorld)
        players[i].status = PlayerStatus.Left
    else
        players[i].status = PlayerStatus.Unknown
}

private fun onPlayerDeath(player: PlayerData) {
    if (lastTime !in roundDatas) return
    val roundData = roundDatas[lastTime]!!
    val players = roundData.roundDetail.players
    val data = players.find { it.name == player.name }
    if (data == null) return
    val i = players.indexOf(data)
    players[i] = player
}

private fun onRoundWon() {
    if (lastTime !in roundDatas) return
    val roundData = roundDatas[lastTime]!!
    roundData.roundDetail = roundData.roundDetail.copy(isWon = WonOrLost.Won)
}

private fun onRoundLost() {
    if (lastTime !in roundDatas) return
    val roundData = roundDatas[lastTime]!!
    roundData.roundDetail = roundData.roundDetail.copy(isWon = WonOrLost.Lost)
}

private fun onRoundDeath() {
    if (lastTime !in roundDatas) return
    val roundData = roundDatas[lastTime]!!
    roundData.roundDetail = roundData.roundDetail.copy(isDeath = true)
}

private fun onKillerSet(terrors: ArrayList<Int>) {
    if (lastTime !in roundDatas) return
    val roundData = roundDatas[lastTime]!!
    roundData.roundDetail = roundData.roundDetail.copy(terrors = terrors)
}

private fun onHideTerrorShowUp(terrorId: Int) {
    if (lastTime !in roundDatas) return
    val roundData = roundDatas[lastTime]!!
    roundData.roundDetail.terrors[0] = terrorId
}

private const val WARNING_KEYWORD = " Warning"
private const val ERROR_KEYWORD = " Error"
private const val EXCEPTION_KEYWORD = " Exception"
private var textColor = Color.White

private fun onReadLine(line: String) {
    if (line.isEmpty())
        return
    val line = line.replace("\n", "")
    if (" - " in line) {
        logs.add(buildAnnotatedString {
            if (textColor != Color.White)
                textColor = Color.White

            when {
                WARNING_KEYWORD in line -> {
                    textColor = Color.Yellow
                    withStyle(style = SpanStyle(color = textColor)) {
                        append(line)
                    }
                }

                ERROR_KEYWORD in line || EXCEPTION_KEYWORD in line -> {
                    textColor = Color.Red
                    withStyle(style = SpanStyle(color = textColor)) {
                        append(line)
                    }
                }

                else -> append(line)
            }
        })
    } else {
        logs[logs.size - 1] = logs.last().plus(buildAnnotatedString {
            withStyle(style = SpanStyle(color = textColor)) {
                append(line)
            }
            append('\n')
        })
    }
}

internal fun handle(logWatcher: LogWatcher) {
    logWatcher.onNextPredictionEvent += ::onNextPrediction
    logWatcher.onRoundStartEvent += ::onRoundStart
    logWatcher.onRoundOverEvent += ::onRoundOver
    logWatcher.onRoundWonEvent += ::onRoundWon
    logWatcher.onRoundLostEvent += ::onRoundLost
    logWatcher.onRoundDeathEvent += ::onRoundDeath
    logWatcher.onJoinRoomEvent += ::onJoinRoom
    logWatcher.onLeftTONEvent += ::onLeftTON
    logWatcher.onLeftRoomEvent += ::onLeftRoom
    logWatcher.onReadLineEvent += ::onReadLine
    logWatcher.onPlayerJoinedRoomEvent += ::onPlayerJoinedRoom
    logWatcher.onPlayerLeftRoomEvent += ::onPlayerLeftRoom
    logWatcher.onPlayerDeathEvent += ::onPlayerDeath
    logWatcher.onKillerSetEvent += ::onKillerSet
    logWatcher.onHideTerrorShowUpEvent += ::onHideTerrorShowUp
}