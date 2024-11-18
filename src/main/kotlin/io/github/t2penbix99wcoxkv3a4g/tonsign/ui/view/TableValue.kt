package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view

abstract class TableValue(open val name: String) {
    abstract val headerText: String
    abstract val columnIndex: Int
    open val isTime: Boolean = false
}

class TimeValue(override val name: String) : TableValue(name) {
    override val headerText = "gui.tab.table_header.log_data.time"
    override val columnIndex = 0
    override val isTime = true
}

class RoundTypeValue(override val name: String): TableValue(name) {
    override val headerText = "gui.tab.table_header.round_data.round_type"
    override val columnIndex = 1
}