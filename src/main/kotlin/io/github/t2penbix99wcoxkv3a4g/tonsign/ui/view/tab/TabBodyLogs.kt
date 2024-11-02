package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.rememberWindowState
import androidx.navigation.NavHostController
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.swapList
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.EventAppender
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.CupcakeEXTheme
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.SearchButton
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.searchField
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.tray.TrayItem
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.tray.TrayLogs
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TabBodyLogs : TabBodyBase() {
    override val title: String
        get() = "gui.tab.title.logs"

    override val id: String
        get() = "logs"

    override val isOnTop: MutableState<Boolean>
        get() = internalIsOnTop

    override val trays: List<TrayItem>
        get() = listOf(TrayLogs())

    internal val internalIsOnTop = mutableStateOf(false)

    private val logs = mutableStateListOf<AnnotatedString>()

    init {
        EventAppender.onLogAppendEvent += ::onLogAppend
    }

    @Composable
    override fun icon() {
        Icon(Icons.Default.Info, contentDescription = title.i18n())
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
        })
    }

    @Composable
    private fun viewAll(
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>,
        isTopWindow: Boolean
    ) {
        val scope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()
        val logs = remember { logs }
        var search by remember { mutableStateOf("") }
        var changedLogs = remember { mutableStateListOf<AnnotatedString>() }
        val autoScrollToDown by remember { mutableStateOf(ConfigManager.config.autoScrollToDown) }
        var isOnTop by remember { internalIsOnTop }

        changedLogs.clear()
        changedLogs.addAll(logs)

        fun searchFilter() {
            val filteredList = changedLogs.filter {
                it.contains(search, ignoreCase = true)
            }
            changedLogs.swapList(filteredList)
        }

        if (search.isNotEmpty())
            searchFilter()

        val searchButtons = if (!isTopWindow) {
            listOf(
                SearchButton({
                    isOnTop = true
                }, Icons.Filled.ArrowOutward, "IsOnTop")
            )
        } else
            listOf()

        searchField(
            search,
            {
                search = it
                searchFilter()
            },
            {
                scope.launch {
                    scrollState.scrollToItem((changedLogs.size - 1).coerceAtLeast(0))
                }
            }, searchButtons
        )

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

                    if (!autoScrollToDown) return@items
                    scope.launch {
                        scrollState.scrollToItem((changedLogs.size - 1).coerceAtLeast(0))
                    }
                }
            }
        }
    }

    @Composable
    override fun view(
        navController: NavHostController,
        padding: PaddingValues,
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        viewAll(trayState, needRestart, needRefresh, false)
    }

    @Composable
    override fun topMenu(trayState: TrayState, needRestart: MutableState<Boolean>, needRefresh: MutableState<Boolean>) {
        val windowState =
            rememberWindowState(position = Aligned(alignment = Alignment.Center), size = DpSize(500.dp, 350.dp))
        var isOnTop by remember { internalIsOnTop }

        if (!isOnTop) return

        Window(
            onCloseRequest = { isOnTop = false },
            visible = true,
            title = "gui.title.window.log_viewer".i18n(),
            state = windowState,
            alwaysOnTop = true
        ) {
            CupcakeEXTheme {
                Column(Modifier.fillMaxWidth()) {
                    viewAll(trayState, needRestart, needRefresh, true)
                }
            }
        }
    }
}