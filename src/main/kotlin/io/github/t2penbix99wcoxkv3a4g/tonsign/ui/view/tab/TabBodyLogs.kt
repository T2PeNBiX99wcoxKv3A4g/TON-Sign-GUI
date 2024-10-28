package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.swapList
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.EventAppender
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.searchField
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TabBodyLogs : TabBodyBase() {
    override val title = { LanguageManager.get("gui.tab.title.logs") }

    //        private val logs = mutableStateOf(AnnotatedString(""))
//    private val logs = mutableStateOf(mutableListOf<AnnotatedString>())
    private val logs = mutableStateListOf<AnnotatedString>()

    init {
        EventAppender.onLogAppendEvent += ::onLogAppend
    }

    private fun getThreadColor(level: Level): Color {
        return when (level.toInt()) {
            Level.ERROR_INT -> Color.Red
            Level.WARN_INT -> Color.Yellow
            else -> Color.Cyan
        }
    }

    private fun getLogLevelColor(level: Level): Color {
        return when (level.toInt()) {
            Level.ERROR_INT -> Color.Red
            Level.WARN_INT -> Color.Yellow
            Level.DEBUG_INT -> Color.Green
            else -> Color.White
        }
    }

    private fun onLogAppend(event: ILoggingEvent) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val current = LocalDateTime.now().format(formatter)

        logs.add(buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Black)) {
                append("[$current]")
            }

            append(' ')
            append('[')

            withStyle(style = SpanStyle(color = getThreadColor(event.level))) {
                append(event.threadName)
            }

            append('/')

            withStyle(style = SpanStyle(color = getLogLevelColor(event.level))) {
                append(event.level.levelStr)
            }

            append(']')
            append(' ')

            withStyle(style = SpanStyle(color = Color.Magenta)) {
                append("(${event.loggerName})")
            }

            append(':')
            append(' ')

            withStyle(style = SpanStyle(color = getLogLevelColor(event.level))) {
                append(event.formattedMessage)
            }

            append('\n')
        })
    }

    @Composable
    override fun view(
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        val scope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()
        val logs = remember { logs }
        var search = remember { mutableStateOf("") }
        var changedLogs = remember { mutableStateListOf<AnnotatedString>() }

        changedLogs.clear()
        changedLogs.addAll(logs)

        fun searchFilter() {
            val filteredList = changedLogs.filter {
                it.contains(search.value, ignoreCase = false)
            }
            changedLogs.swapList(filteredList)
        }

        if (search.value.isNotEmpty())
            searchFilter()

        searchField(search.value) {
            search.value = it

            searchFilter()
        }

        SelectionContainer {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .fillMaxWidth()
                    .padding(10.dp),
                state = scrollState
            ) {
                items(changedLogs.size) {
                    Box(
                        Modifier.fillMaxWidth()
                            .background(Color(0, 0, 0, 40))
                            .padding(start = 10.dp, end = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(changedLogs[it])
                    }

                    scope.launch {
                        scrollState.scrollToItem((changedLogs.size - 1).coerceAtLeast(0))
                    }
                }
            }
        }
    }
}