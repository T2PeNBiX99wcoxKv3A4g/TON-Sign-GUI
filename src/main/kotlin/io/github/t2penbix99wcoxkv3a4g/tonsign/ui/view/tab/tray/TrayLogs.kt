package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.tray

import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyBase
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyLogs

class TrayLogs(override val label: String = "gui.title.window.log_viewer") : TrayItem() {
    override fun onClick(tabBody: TabBodyBase) {
        if (tabBody !is TabBodyLogs) return
        tabBody.internalIsOnTop.value = true
    }
}