package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.rememberWindowState
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.swapList
import io.github.t2penbix99wcoxkv3a4g.tonsign.logs
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.MaterialEXTheme
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.SearchButton
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.searchField
import kotlinx.coroutines.launch

class TabBodyVRChatLogs : TabBodyBase() {
    override val title: String
        get() = "gui.tab.title.vrchat_logs"

    override val isOnTop: MutableState<Boolean>
        get() = _isOnTop

    override val trayName: String?
        get() = "VRChat Log Viewer"

    private val _isOnTop = mutableStateOf(false)

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
        var isOnTop by remember { _isOnTop }

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
                }, Icons.Filled.Menu, "IsOnTop")
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
                            .fillMaxHeight()
                            .background(Color(0, 0, 0, 20))
                            .padding(start = 10.dp),
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
        var isOnTop by remember { _isOnTop }

        if (!isOnTop) return

        Window(
            onCloseRequest = { isOnTop = false },
            visible = true,
            title = LanguageManager.get("gui.title.window.vrchat_log_viewer"),
            state = windowState,
            alwaysOnTop = true
        ) {
            MaterialEXTheme {
                Column(Modifier.fillMaxWidth()) {
                    viewAll(trayState, needRestart, needRefresh, true)
                }
            }
        }
    }

    override fun trayClick(
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        _isOnTop.value = true
    }
}