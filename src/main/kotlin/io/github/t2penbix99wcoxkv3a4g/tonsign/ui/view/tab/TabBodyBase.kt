package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.TrayState

abstract class TabBodyBase {
    open val title: String
        get() = "Base"

    open val isOnTop: MutableState<Boolean>
        get() = mutableStateOf(false)

    open val maxWidth: Float
        get() = 1f
    
    open val trayName: String?
        get() = null

    @Composable
    abstract fun view(trayState: TrayState, needRestart: MutableState<Boolean>, needRefresh: MutableState<Boolean>)

    @Composable
    open fun detailView(trayState: TrayState, needRestart: MutableState<Boolean>, needRefresh: MutableState<Boolean>) {
    }

    @Composable
    open fun topMenu(trayState: TrayState, needRestart: MutableState<Boolean>, needRefresh: MutableState<Boolean>) {
    }
    
    open fun trayClick(trayState: TrayState, needRestart: MutableState<Boolean>, needRefresh: MutableState<Boolean>) {
    }
}