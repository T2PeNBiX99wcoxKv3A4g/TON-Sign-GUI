package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.tray

import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyBase

abstract class TrayItem() {
    abstract val label: String
    open val isEnabled
        get() = true

    abstract fun onClick(tabBody: TabBodyBase)
}