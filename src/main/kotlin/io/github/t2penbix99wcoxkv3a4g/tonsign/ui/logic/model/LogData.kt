package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model

import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.TableHeader

data class LogData(
    @TableHeader("gui.tab.table_header.log_data.time", 0)
    val time: String,
    @TableHeader("gui.tab.table_header.log_data.thread_name", 1)
    val threadName: String,
    @TableHeader("gui.tab.table_header.log_data.level", 2)
    val level: String,
    @TableHeader("gui.tab.table_header.log_data.name", 3)
    val name: String,
    @TableHeader("gui.tab.table_header.log_data.msg", 4)
    val msg: String
)