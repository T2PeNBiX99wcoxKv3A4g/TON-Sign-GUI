package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.tray

import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.TrayState
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyBase
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyVRChatLogs

class TrayVRChatLogs(override val label: String = "gui.title.window.vrchat_log_viewer") : TrayItem() {
    override fun onClick(
        tabBody: TabBodyBase,
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        if (tabBody !is TabBodyVRChatLogs) return
        tabBody.internalIsOnTop.value = true
    }
}