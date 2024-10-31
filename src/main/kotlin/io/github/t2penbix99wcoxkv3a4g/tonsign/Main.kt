package io.github.t2penbix99wcoxkv3a4g.tonsign

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.kdroid.composetray.tray.api.Tray
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18nByLang
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.app
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.showConfirmExitWindow
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.showNeedRestartWindows
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.CupcakeEXTheme
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.SelectionState

internal val trayState = TrayState()

fun main() = application {
    var isOpen = remember { mutableStateOf(true) }
    var isOpenSet by isOpen
    val isAskingToClose = remember { mutableStateOf(false) }
    var isAskingToCloseSet by isAskingToClose
    var isVisible by remember { mutableStateOf(true) }
    val needRefresh = remember { mutableStateOf(false) }
    val needRestart = remember { mutableStateOf(false) }
    val windowState =
        rememberWindowState(position = Aligned(alignment = Alignment.Center), size = DpSize(800.dp, 600.dp))
    val refreshWindowState =
        rememberWindowState(position = Aligned(alignment = Alignment.Center), size = DpSize(300.dp, 260.dp))
    var needRestartSet by needRestart
    var needRefreshSet by needRefresh
    val trayState by remember { mutableStateOf(trayState) }
    val onTop by remember { mutableStateOf(ConfigManager.config.onTop) }

    startWatcher()

    if (isAskingToCloseSet && !isVisible)
        isVisible = true

    if (needRestartSet && !isVisible)
        isVisible = true

    if (isOpenSet) {
        Tray(
            iconPath = "", // TODO: Icon
            windowsIconPath = "",
            tooltip = Utils.TITLE,
            primaryAction = {
                if (!isVisible)
                    isVisible = true
            },
            primaryActionLinuxLabel = "Open Application"
        ) {
            SelectionState.entries.forEach {
                val gui = it.gui

                if (gui.trays.isNotEmpty()) {
                    // TODO: ??? Japanese not supported?
                    SubMenu(gui.title.i18nByLang("en")) {
                        gui.trays.forEach {
                            Item(it.label.i18nByLang("en"), it.isEnabled) {
                                it.onClick(gui, trayState, needRestart, needRefresh)
                            }
                        }
                    }
                }
            }
            Divider()
            Item("gui.tray.exit".i18n()) {
                isAskingToCloseSet = true
            }
        }

        if (!needRefreshSet) {
            Window(
                onCloseRequest = { isVisible = false },
                visible = isVisible,
                title = Utils.TITLE,
                state = windowState,
                alwaysOnTop = onTop
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
                state = refreshWindowState
            ) {
                CupcakeEXTheme {
                    Text("Refreshing...")
                    ConfigManager.save()
                    needRefreshSet = false
                }
            }
        }

        SelectionState.entries.forEach {
            val gui = it.gui
            var isOnTop by remember { gui.isOnTop }

            if (isOnTop) {
                gui.topMenu(trayState, needRestart, needRefresh)
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
