package io.github.t2penbix99wcoxkv3a4g.tonsign.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.MaterialEXTheme

private val dialogSize = DpSize(280.dp, 180.dp)

@Composable
fun openDialogWindow(
    title: () -> String,
    msg: () -> String,
    yesText: () -> String,
    noText: () -> String,
    yesDo: () -> Unit,
    noDo: () -> Unit
) {
    val state = rememberDialogState(position = Aligned(alignment = Alignment.Center), size = dialogSize)
    DialogWindow(
        onCloseRequest = { noDo() },
        title = title(),
        state = state
    ) {
        MaterialEXTheme {
            Column(
                modifier = Modifier.padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(msg())
                Row(
                    modifier = Modifier.padding(20.dp)
                        .fillMaxSize()
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.aligned(Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = { yesDo() }
                    ) {
                        Text(yesText())
                    }
                    Button(
                        onClick = { noDo() }
                    ) {
                        Text(noText())
                    }
                }
            }
        }
    }
}

@Composable
fun openDialogWindow(title: () -> String, msg: () -> String, yesDo: () -> Unit, noDo: () -> Unit) {
    openDialogWindow(
        title,
        msg,
        { LanguageManager.getState("gui.button.yes").value },
        { LanguageManager.getState("gui.button.no").value },
        yesDo,
        noDo
    )
}

@Composable
fun showConfirmExitWindow(isAskingToClose: MutableState<Boolean>, isOpen: MutableState<Boolean>) {
    var isAskingToCloseSet by isAskingToClose
    var isOpenSet by isOpen

    openDialogWindow(
        title = { LanguageManager.getState("gui.title.confirm_exit").value },
        msg = { LanguageManager.getState("gui.text.confirm_exit").value },
        yesDo = {
            onExit()
            isOpenSet = false
        },
        noDo = {
            isAskingToCloseSet = false
        }
    )
}

@Composable
fun showNeedRestartWindows(needRestart: MutableState<Boolean>, isOpen: MutableState<Boolean>) {
    var needRestartSet by needRestart
    var isOpenSet by isOpen

    openDialogWindow(
        title = { LanguageManager.getState("gui.title.need_restart").value },
        msg = { LanguageManager.getState("gui.text.need_restart").value },
        yesDo = {
            onExit()
            isOpenSet = false
        },
        noDo = {
            needRestartSet = false
        }
    )
}