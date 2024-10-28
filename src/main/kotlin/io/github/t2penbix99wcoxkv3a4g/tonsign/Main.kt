package io.github.t2penbix99wcoxkv3a4g.tonsign

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberNotification
import androidx.compose.ui.window.rememberWindowState
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.LogicScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventArg
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.app
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.nextPrediction
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.showConfirmExitWindow
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.showNeedRestartWindows
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.MaterialEXTheme
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.VRChatWatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var logWatcherIsStarted = false
private var runningTime = 0
private val logicScope = LogicScope()
private var needToWait = false
private val trayState = TrayState()
internal val logs = mutableStateListOf<AnnotatedString>()
val readerInitEvent = EventArg<LogWatcher>()

private fun startReader() {
    if (logWatcherIsStarted)
        return

    logicScope.launch {
        while (true) {
            runCatching {
                if (!VRChatWatcher.isVRChatRunning()) {
                    if (!needToWait)
                        needToWait = true
                    VRChatWatcher.checkVRChatLoop()
                }

                if (needToWait) {
                    Logger.info("log.wait_until_join_game")
                    delay(60000)
                }

                runningTime++
                logWatcher = LogWatcher.Default
                logWatcher!!.onNextPredictionEvent += ::onNextPrediction
                logWatcher!!.onRoundOverEvent += ::onRoundOver
                logWatcher!!.onLeftTONEvent += ::onLeftTON
                logWatcher!!.onReadLineEvent += ::onReadLine
                readerInitEvent(logWatcher!!)
                logWatcher!!.monitorRoundType()
            }.getOrElse {
                Logger.error(it, "exception.something_is_not_right", it.message!!)
                needToWait = false
            }
        }
    }

    logWatcherIsStarted = true
}

private fun onNextPrediction(guessRound: GuessRoundType) {
    nextPrediction.value = guessRound
}

private fun onLeftTON() {
    nextPrediction.value = GuessRoundType.NIL
}

private fun onRoundOver(guessRound: GuessRoundType) {
    if (guessRound == GuessRoundType.NIL || guessRound == GuessRoundType.Exempt) return
    if (ConfigManager.config.onlySpecial && guessRound == GuessRoundType.Classic) return

    val special = LanguageManager.get("gui.text.round_special")
    val classic = LanguageManager.get("gui.text.round_classic")

    val nextPredictionNotification = Notification(
        LanguageManager.get("gui.notification.next_prediction_title"),
        LanguageManager.get(
            "gui.notification.next_prediction_message",
            if (guessRound == GuessRoundType.Special) special else classic
        )
    )

    trayState.sendNotification(nextPredictionNotification)
}

private const val WARNING_KEY_WORD = " Warning"
private const val ERROR_KEY_WORD = " Error"
private const val EXCEPTION_KEY_WORD = " Exception"
private var textColor = Color.White

private fun onReadLine(line: String) {
    logs.add(buildAnnotatedString {
        if (" - " in line) {
            if (textColor != Color.White)
                textColor = Color.White

            when {
                WARNING_KEY_WORD in line -> {
                    textColor = Color.Yellow
                    withStyle(style = SpanStyle(color = textColor)) {
                        append(line)
                    }
                }

                ERROR_KEY_WORD in line || EXCEPTION_KEY_WORD in line -> {
                    textColor = Color.Red
                    withStyle(style = SpanStyle(color = textColor)) {
                        append(line)
                    }
                }

                else -> append(line)
            }
        } else {
            withStyle(style = SpanStyle(color = textColor)) {
                append(line)
            }
        }

        append('\n')
    })
}

fun main() = application {
    var isOpen = remember { mutableStateOf(true) }
    var isOpenSet by isOpen
    val isAskingToClose = remember { mutableStateOf(false) }
    var isAskingToCloseSet by isAskingToClose
    var isVisible by remember { mutableStateOf(true) }
    val needRefresh = remember { mutableStateOf(false) }
    val needRestart = remember { mutableStateOf(false) }
    val notification = rememberNotification("Notification", "Message from MyApp!")
    val windowState =
        rememberWindowState(position = Aligned(alignment = Alignment.Center), size = DpSize(800.dp, 600.dp))
    val windowState2 =
        rememberWindowState(position = Aligned(alignment = Alignment.Center), size = DpSize(300.dp, 260.dp))
    var needRestartSet by needRestart
    var needRefreshSet by needRefresh
    val trayState by remember { mutableStateOf(trayState) }

    startReader()

    if (isAskingToCloseSet && !isVisible)
        isVisible = true

    if (needRestartSet && !isVisible)
        isVisible = true

    if (isOpenSet) {
        Tray(
            state = trayState,
            icon = TrayIcon,
            tooltip = Utils.TITLE,
            onAction = {
                if (!isVisible)
                    isVisible = true
            },
            menu = {
                MaterialTheme(colors = if (isSystemInDarkTheme()) darkColors() else lightColors()) {
                    Item(
                        "Send notification",
                        onClick = {
                            trayState.sendNotification(notification)
                        }
                    )
                    Item(
//                        LanguageManager.getState("gui.tray.exit").value,
                        "Exit",
                        onClick = {
                            isAskingToCloseSet = true
                        }
                    )
                }
            }
        )

        if (!needRefreshSet) {
            Window(
                onCloseRequest = { isVisible = false },
                visible = isVisible,
                title = Utils.TITLE,
                state = windowState
            ) {
                app(trayState, needRestart, needRefresh)

                if (isAskingToCloseSet)
                    showConfirmExitWindow(isAskingToClose, isOpen)

                if (needRestartSet)
                    showNeedRestartWindows(needRestart, isOpen)
            }
        } else {
            Window(
                onCloseRequest = { needRefreshSet = false },
                visible = true,
                title = "Refreshing...",
                state = windowState2
            ) {
                MaterialEXTheme {
                    Text("Refreshing...")
                    ConfigManager.save()
                    needRefreshSet = false
                }
            }
        }
    }
}

object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
    }
}
