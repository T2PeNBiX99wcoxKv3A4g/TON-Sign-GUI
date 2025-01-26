package io.github.t2penbix99wcoxkv3a4g.tonsign.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.rememberDialogState
import io.github.t2penbix99wcoxkv3a4g.tonsign.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.l10nState
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.CupcakeEXTheme

private val dialogSize = DpSize(280.dp, 180.dp)
private val dialogSize2 = DpSize(680.dp, 520.dp)

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
    val yes by "gui.button.dialog.yes".l10nState()
    val no by "gui.button.dialog.no".l10nState()

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
    val confirmExitTitle by "gui.title.dialog.confirm_exit".l10nState()
    val confirmExitText by "gui.text.dialog.confirm_exit".l10nState()

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
    val needRestartTitle by "gui.title.dialog.need_restart".l10nState()
    val needRestartText by "gui.text.dialog.need_restart".l10nState()

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

@Composable
fun showErrorWindows() {
    val title by "gui.title.dialog.error".l10nState()
    val text by "gui.text.dialog.error".l10nState()
    val yes by "gui.button.dialog.yes".l10nState()
    val logicError by remember { logicError }
    var logicHasError by remember { logicHasError }
    val state = rememberDialogState(position = Aligned(alignment = Alignment.Center), size = dialogSize2)
    val scrollState = rememberScrollState()
    DialogWindow(
        onCloseRequest = { logicHasError = false },
        title = title,
        state = state,
        alwaysOnTop = true
    ) {
        CupcakeEXTheme {
            Column(
                modifier = Modifier.padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text)
                Spacer(Modifier.weight(0.1f))
                SelectionContainer {
                    Box(
                        Modifier.fillMaxWidth()
                            .background(Color(0, 0, 0, 40))
                            .padding(start = 10.dp, end = 10.dp)
                            .height(300.dp)
                            .verticalScroll(scrollState),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append(logicError.stackTraceToString())
                            }
                        })
                    }
                }
                Spacer(Modifier.weight(0.1f))
                Button(
                    onClick = { logicHasError = false }
                ) {
                    Text(yes)
                }
            }
        }
    }
}