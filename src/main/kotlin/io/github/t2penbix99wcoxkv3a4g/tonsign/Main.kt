package io.github.t2penbix99wcoxkv3a4g.tonsign

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberNotification
import androidx.compose.ui.window.rememberTrayState
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.LogicScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.app
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.lastPrediction
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.reader
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.MaterialEXTheme
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.VRChatWatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var readerIsStarted = false
var runningTime = 0
val logicScope = LogicScope()
var needToWait = false

private fun startReader() {
    if (readerIsStarted)
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
                reader = LogWatcher.Default
                reader!!.monitorRoundType()
            }.getOrElse {
                Logger.error(it, "exception.something_is_not_right", it.message!!)
                needToWait = false
            }
        }
    }

    readerIsStarted = true
}

fun main() = application {
    var isOpen by remember { mutableStateOf(true) }
    var isAskingToClose by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(true) }
    val trayState = rememberTrayState()
    val notification = rememberNotification("Notification", "Message from MyApp!")
    var lastPrediction by remember { mutableStateOf(lastPrediction) }

    isSystemInDarkTheme()
    startReader()

    Logger.debug("isSystemInDarkTheme(): ${isSystemInDarkTheme()}")

    if (isOpen) {
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
                        "Exit",
                        onClick = {
                            if (!isVisible)
                                isVisible = true
                            isAskingToClose = true
                        }
                    )

                    if (reader != null && reader!!.nextRoundGuess != GuessRoundType.NIL) {
                        val next = reader!!.nextRoundGuess

                        if (next != lastPrediction) {
                            lastPrediction = next

                            val nextNotification = rememberNotification("Next Round", "Next round is $next")
                            trayState.sendNotification(nextNotification)
                        }
                    }
                }
            }
        )

        Window(onCloseRequest = { isVisible = false }, visible = isVisible, title = Utils.TITLE) {
            app(trayState)

            if (isAskingToClose) {
                DialogWindow(
                    onCloseRequest = { isAskingToClose = false },
                    title = "Test"
                ) {
                    MaterialEXTheme {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                modifier = Modifier.padding(50.dp)
                            ) {
                                Button(
                                    onClick = { isOpen = false }
                                ) {
                                    Text("Yes")
                                }
                                Button(
                                    onClick = { isAskingToClose = false }
                                ) {
                                    Text("No")
                                }
                            }
                        }
                    }
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
