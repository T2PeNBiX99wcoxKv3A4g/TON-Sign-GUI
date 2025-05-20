package io.github.t2penbix99wcoxkv3a4g.tonsign

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.l10n
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.app
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.showConfirmExitWindow
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.showErrorWindows
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.showNeedRestartWindows
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.CupcakeEXTheme
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.SelectionState

internal val trayState = TrayState()
internal val isOpen = mutableStateOf(true)
internal val isAskingToClose = mutableStateOf(false)
internal val needRefresh = mutableStateOf(false)
internal val needRestart = mutableStateOf(false)

fun main() = application {
    val isOpen by remember { isOpen }
    var isAskingToClose by remember { isAskingToClose }
    var isVisible by remember { mutableStateOf(true) }
    var needRefresh by remember { needRefresh }
    val needRestart by remember { needRestart }
    val logicHasError by remember { logicHasError }
    val windowState =
        rememberWindowState(position = Aligned(alignment = Alignment.Center), size = DpSize(800.dp, 600.dp))
    val refreshWindowState =
        rememberWindowState(position = Aligned(alignment = Alignment.Center), size = DpSize(300.dp, 260.dp))
    val onTop by remember { mutableStateOf(ConfigManager.config.onTop) }

    SideEffect {
        startWatcher()
    }

    if (isAskingToClose && !isVisible)
        isVisible = true

    if (needRestart && !isVisible)
        isVisible = true

    if (isOpen) {
        Tray(
            iconContent = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "",
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            },
            tooltip = Utils.TITLE,
            primaryAction = {
                if (!isVisible)
                    isVisible = true
            },
            primaryActionLinuxLabel = "Open"
        ) {
            SelectionState.entries.forEach {
                val gui = it.gui
                if (!gui.enabled) return@forEach
                if (gui.trays.isNotEmpty()) {
                    SubMenu(gui.title.l10n()) {
                        gui.trays.forEach { item ->
                            Item(item.label.l10n(), isEnabled = item.isEnabled) {
                                item.onClick(gui)
                            }
                        }
                    }
                }
            }
            Divider()
            Item("中文測試") {}
            Item("gui.tray.exit".l10n()) {
                isAskingToClose = true
            }
        }

        if (!needRefresh) {
            Window(
                onCloseRequest = { isVisible = false },
                visible = isVisible,
                title = Utils.TITLE,
                state = windowState,
                alwaysOnTop = onTop
            ) {
                app()

                if (isAskingToClose)
                    showConfirmExitWindow()

                if (needRestart)
                    showNeedRestartWindows()

                if (logicHasError)
                    showErrorWindows()
            }
        } else {
            Window(
                onCloseRequest = { needRefresh = false },
                visible = true,
                title = "Refreshing...",
                state = refreshWindowState
            ) {
                CupcakeEXTheme {
                    Text("Refreshing...")
                    ConfigManager.save()
                    needRefresh = false
                }
            }
        }

        SelectionState.entries.forEach {
            val gui = it.gui
            if (!gui.enabled) return@forEach
            val isOnTop by remember { gui.isOnTop }

            if (isOnTop) {
                gui.topMenu()
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
