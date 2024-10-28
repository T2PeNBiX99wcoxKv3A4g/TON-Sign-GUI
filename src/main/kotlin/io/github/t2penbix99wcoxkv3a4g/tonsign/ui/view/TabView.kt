package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.TrayState
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils

// https://github.com/olk90/compose-tableView/blob/main/src/main/kotlin/de/olk90/tableview/view/TabView.kt

@Composable
internal fun tableSelection(
    trayState: TrayState,
    needRestart: MutableState<Boolean>,
    needRefresh: MutableState<Boolean>
) {
    val tableState = remember { mutableStateOf(SelectionState.Main) }

    Column {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = Utils.TITLE) },
                    actions = {
                    }
                )
            },
            content = {
                Column {
                    tabs(tableState)
                    tableBody(tableState, trayState, needRestart, needRefresh)
                }
            }
        )
    }
}

@Composable
fun tabs(tableState: MutableState<SelectionState>) {
    TabRow(selectedTabIndex = tableState.value.ordinal) {
        SelectionState.entries.forEach {
//            LeadingIconTab()
            Tab(text = { Text(it.gui.title()) }, selected = tableState.value == it, onClick = {
                tableState.value = it
            })
        }
    }
}

@Composable
fun tableBody(
    tableState: MutableState<SelectionState>,
    trayState: TrayState,
    needRestart: MutableState<Boolean>,
    needRefresh: MutableState<Boolean>
) {
    Row {
        Column(Modifier.fillMaxWidth()) {
            tableState.value.gui.view(trayState, needRestart, needRefresh)
        }
    }
}