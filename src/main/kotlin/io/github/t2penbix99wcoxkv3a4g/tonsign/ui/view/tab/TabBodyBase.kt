package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.TrayState

abstract class TabBodyBase {
    open val title = { "Base" }

    @Composable
    abstract fun view(trayState: TrayState, needRestart: MutableState<Boolean>, needRefresh: MutableState<Boolean>)
}