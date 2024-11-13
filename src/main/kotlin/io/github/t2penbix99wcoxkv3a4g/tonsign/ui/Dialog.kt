package io.github.t2penbix99wcoxkv3a4g.tonsign.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.rememberDialogState
import io.github.t2penbix99wcoxkv3a4g.tonsign.isAskingToClose
import io.github.t2penbix99wcoxkv3a4g.tonsign.isOpen
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18nState
import io.github.t2penbix99wcoxkv3a4g.tonsign.needRestart
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.CupcakeEXTheme

private val dialogSize = DpSize(280.dp, 180.dp)

@Composable
fun openDialogWindow(
    title: String,
    msg: String,
    yesText: String,
    noText: String,
    yesDo: () -> Unit,
    noDo: () -> Unit
) {
    val state = rememberDialogState(position = Aligned(alignment = Alignment.Center), size = dialogSize)
    DialogWindow(
        onCloseRequest = { noDo() },
        title = title,
        state = state
    ) {
        CupcakeEXTheme {
            Column(
                modifier = Modifier.padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(msg)
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
                        Text(yesText)
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = { noDo() }
                    ) {
                        Text(noText)
                    }
                }
            }
        }
    }
}

@Composable
fun openDialogWindow(title: String, msg: String, yesDo: () -> Unit, noDo: () -> Unit) {
    val yes by "gui.button.dialog.yes".i18nState()
    val no by "gui.button.dialog.no".i18nState()

    openDialogWindow(
        title,
        msg,
        yes,
        no,
        yesDo,
        noDo
    )
}

@Composable
fun showConfirmExitWindow() {
    var isAskingToClose by remember { isAskingToClose }
    var isOpen by remember { isOpen }
    val confirmExitTitle by "gui.title.dialog.confirm_exit".i18nState()
    val confirmExitText by "gui.text.dialog.confirm_exit".i18nState()

    openDialogWindow(
        title = confirmExitTitle,
        msg = confirmExitText,
        yesDo = {
            onExit()
            isOpen = false
        },
        noDo = {
            isAskingToClose = false
        }
    )
}

@Composable
fun showNeedRestartWindows() {
    var needRestart by remember { needRestart }
    var isOpen by remember { isOpen }
    val needRestartTitle by "gui.title.dialog.need_restart".i18nState()
    val needRestartText by "gui.text.dialog.need_restart".i18nState()

    openDialogWindow(
        title = needRestartTitle,
        msg = needRestartText,
        yesDo = {
            onExit()
            isOpen = false
        },
        noDo = {
            needRestart = false
        }
    )
}