package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.TrayState

abstract class TabBodyBase {
    open val title: String
        get() {
            return "Base"
        }
    open val onTop: MutableState<Boolean>
        get() {
            return mutableStateOf(false)
        }
    open val maxWidth: Float
        get() {
            return 1f
        }

    @Composable
    abstract fun view(trayState: TrayState, needRestart: MutableState<Boolean>, needRefresh: MutableState<Boolean>)

    @Composable
    open fun detailView(trayState: TrayState, needRestart: MutableState<Boolean>, needRefresh: MutableState<Boolean>) {
    }

    @Composable
    open fun onTopDo(trayState: TrayState, needRestart: MutableState<Boolean>, needRefresh: MutableState<Boolean>) {
    }
}