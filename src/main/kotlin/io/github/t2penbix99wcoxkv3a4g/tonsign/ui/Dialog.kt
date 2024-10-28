package io.github.t2penbix99wcoxkv3a4g.tonsign.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.rememberDialogState
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.MaterialEXTheme

private fun onExit() {
    ConfigManager.save()
}

val dialogSize = DpSize(250.dp, 180.dp)

@Composable
fun showConfirmExitWindow(isAskingToClose: MutableState<Boolean>, isOpen: MutableState<Boolean>) {
    var isAskingToCloseSet by isAskingToClose
    var isOpenSet by isOpen
    val state = rememberDialogState(position = Aligned(alignment = Alignment.Center), size = dialogSize)
    DialogWindow(
        onCloseRequest = { isAskingToCloseSet = false },
        title = LanguageManager.getState("gui.title.confirm_exit").value,
        state = state
    ) {
        MaterialEXTheme {
            Column(
                modifier = Modifier.padding(10.dp).fillMaxSize()
            ) {
                Text(LanguageManager.getState("gui.text.confirm_exit").value)
                Button(
                    onClick = {
                        onExit()
                        isOpenSet = false
                    }
                ) {
                    Text(LanguageManager.getState("gui.button.yes").value)
                }
                Button(
                    onClick = { isAskingToCloseSet = false }
                ) {
                    Text(LanguageManager.getState("gui.button.no").value)
                }
            }
        }
    }
}

@Composable
fun showNeedRestartWindows(needRestart: MutableState<Boolean>, isOpen: MutableState<Boolean>) {
    var needRestartSet by needRestart
    var isOpenSet by isOpen
    val state = rememberDialogState(position = Aligned(alignment = Alignment.Center), size = dialogSize)
    DialogWindow(
        onCloseRequest = { needRestartSet = false },
        title = LanguageManager.getState("gui.title.need_restart").value,
        state = state
    ) {
        MaterialEXTheme {
            Column(
                modifier = Modifier.padding(10.dp).fillMaxSize()
            ) {
                Text(LanguageManager.getState("gui.text.need_restart").value)
                Button(
                    onClick = {
                        onExit()
                        isOpenSet = false
                    }
                ) {
                    Text(LanguageManager.getState("gui.button.yes").value)
                }
                Button(
                    onClick = { needRestartSet = false }
                ) {
                    Text(LanguageManager.getState("gui.button.no").value)
                }
            }
        }
    }
}