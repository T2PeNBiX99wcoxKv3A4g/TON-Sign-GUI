package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.swapList
import io.github.t2penbix99wcoxkv3a4g.tonsign.logs
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.searchField
import kotlinx.coroutines.launch

class TabBodyVRChatLogs : TabBodyBase() {
    override val title = { "gui.tab.title.vrchat_logs" }

    @Composable
    override fun view(
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        val scope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()
        val logs = remember { logs }
        var search by remember { mutableStateOf("") }
        var changedLogs = remember { mutableStateListOf<AnnotatedString>() }
        val autoScrollToDown by remember { mutableStateOf(ConfigManager.config.autoScrollToDown) }

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

        searchField(search, {
            search = it
            searchFilter()
        }) {
            scope.launch {
                scrollState.scrollToItem((changedLogs.size - 1).coerceAtLeast(0))
            }
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
}