package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.TrayState
import androidx.navigation.NavHostController
import ch.qos.logback.classic.spi.ILoggingEvent
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.EventAppender
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.LogData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tableView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TabBodyLogDatas : TabBodyBase() {
    override val title: String
        get() = "gui.tab.title.log_datas"
    override val id: String
        get() = "log_datas"
    
    private val logDatas = mutableStateListOf<LogData>()

    init {
        EventAppender.onLogAppendEvent += ::onLogAppend
    }

    private fun onLogAppend(event: ILoggingEvent) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val current = LocalDateTime.now().format(formatter)

        logDatas.add(
            LogData(
                current,
                event.threadName,
                event.level.levelStr,
                event.loggerName,
                event.formattedMessage
            )
        )
    }

    @Composable
    override fun icon() {
        Icon(Icons.Default.Check, contentDescription = title.i18n())
    }

    @Composable
    override fun view(
        navController: NavHostController,
        padding: PaddingValues,
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        val logData: MutableState<LogData?> = remember { mutableStateOf(null) }

        tableView(logData, logDatas, onRowSelection = {
            logData.value = it
        })
    }
}