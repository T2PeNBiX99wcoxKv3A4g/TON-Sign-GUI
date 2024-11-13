package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.tray

import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyBase
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyRoundDatas

class TrayRoundDatas(override val label: String = "Round Data Viewer") : TrayItem() {
    override fun onClick(tabBody: TabBodyBase) {
        if (tabBody !is TabBodyRoundDatas) return
        tabBody.internalIsOnTop.value = true
    }
}