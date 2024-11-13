package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.tray

import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyBase
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyVRChatLogs

class TrayVRChatLogs(override val label: String = "gui.title.window.vrchat_log_viewer") : TrayItem() {
    override fun onClick(tabBody: TabBodyBase) {
        if (tabBody !is TabBodyVRChatLogs) return
        tabBody.internalIsOnTop.value = true
    }
}