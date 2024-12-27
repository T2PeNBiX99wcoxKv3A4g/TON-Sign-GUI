package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.rememberWindowState
import androidx.navigation.NavHostController
import ch.qos.logback.classic.Level
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventBus
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.Events
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.OnLogAppendEvent
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.Subscribe
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.swapList
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
        EventBus.register(this)
    }

    @Composable
    override fun icon() {
        Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = title.i18n())
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

    @Subscribe(Events.OnLogAppend)
    private fun onLogAppend(event: OnLogAppendEvent) {
        val logEvent = event.loggingEvent
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val current = LocalDateTime.now().format(formatter)

        logs.add(buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Black)) {
                append("[$current]")
            }

            append(' ')
            append('[')

            withStyle(style = SpanStyle(color = getThreadColor(logEvent.level))) {
                append(logEvent.threadName)
            }

            append('/')

            withStyle(style = SpanStyle(color = getLogLevelColor(logEvent.level))) {
                append(logEvent.level.levelStr)
            }

            append(']')
            append(' ')

            withStyle(style = SpanStyle(color = Color.Magenta)) {
                append("(${logEvent.loggerName})")
            }

            append(':')
            append(' ')

            withStyle(style = SpanStyle(color = getLogLevelColor(logEvent.level))) {
                append(logEvent.formattedMessage)
            }
        })
    }

    @Suppress("unused")
    @Composable
    private fun viewAll(isTopWindow: Boolean) {
        val scope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()
        val logs = remember { logs }
        var search by remember { mutableStateOf("") }
        val changedLogs = remember { mutableStateListOf<AnnotatedString>() }
        val autoScrollToDown by remember { mutableStateOf(ConfigManager.config.autoScrollToDown) }
        var isOnTop by remember { internalIsOnTop }

        changedLogs.swapList(logs)

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
                }, Icons.AutoMirrored.Filled.OpenInNew, "IsOnTop")
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
    override fun view(navController: NavHostController, padding: PaddingValues) {
        viewAll(false)
    }

    @Composable
    override fun topMenu() {
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
                    viewAll(true)
                }
            }
        }
    }
}