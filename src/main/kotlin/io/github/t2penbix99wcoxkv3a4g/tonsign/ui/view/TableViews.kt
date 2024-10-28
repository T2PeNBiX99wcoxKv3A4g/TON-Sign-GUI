package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.swapList
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import kotlin.collections.sortedBy
import kotlin.collections.sortedByDescending
import kotlin.reflect.KCallable

// https://github.com/olk90/compose-tableView/blob/main/src/main/kotlin/de/olk90/tableview/view/TableViews.kt

fun <T> searchFilter(
    tabContent: SnapshotStateList<T>,
    fields: List<KCallable<*>>,
    search: MutableState<String>
) {
    val filteredList = tabContent.filter {
        val values = fields.map { kc ->
            val value = kc.call(it)
            if (value == null) {
                ""
            } else {
                "$value"
            }
        }
        values.any { f -> f.contains(search.value, true) }
    }

    tabContent.swapList(filteredList)
}

@Composable
inline fun <reified T : Any> tableView(
    currentItem: MutableState<T?>,
    content: SnapshotStateList<T>,
    indexColumn: Boolean = false,
    indexColWidth: Dp = 30.dp,
    noinline onRowSelection: (T) -> Unit
) {
    val fields = T::class.members.filter {
        it.annotations.any { a -> a is TableHeader }
    }.sortedBy {
        val header = getTableHeader(it.annotations)
        header.columnIndex
    }

    var search = remember { mutableStateOf("") }
    val tableContent = remember { content }
    val tabContent = remember { mutableStateListOf<T>() }

    tabContent.clear()
    tabContent.addAll(tableContent)

    if (search.value.isNotEmpty())
        searchFilter(tabContent, fields, search)

    val onContentUpdate: (String) -> Unit = {
        search.value = it
        searchFilter(tabContent, fields, search)
    }

    Column(
        Modifier.fillMaxWidth()
    ) {
        searchField(search.value, onContentUpdate)

        val headerList = fields
            .flatMap { it.annotations }
            .filter { it is TableHeader && it.columnIndex >= 0 }
            .map { it as TableHeader }

        val stateMap = headerList.associateWith { mutableStateOf(SortingState.NONE) }
        val sortingStates = remember { mutableStateOf(stateMap) }

        val onSortingUpdate: (TableHeader, SortingState) -> Unit = { tableHeader, sortingState ->
            val sortedList: List<T> = when (sortingState) {
                SortingState.ASC -> {
                    tabContent.sortedBy { t ->
                        sort(t, tableHeader)
                    }
                }

                SortingState.DESC -> {
                    tabContent.sortedByDescending { t ->
                        sort(t, tableHeader)
                    }
                }

                else -> {
                    tabContent
                }
            }

            tabContent.swapList(sortedList)
        }

        tableHeader(indexColumn, indexColWidth, headerList, sortingStates, onSortingUpdate)
        tableContent(currentItem, tabContent, indexColumn, indexColWidth, onRowSelection)
    }
}

@Composable
fun searchField(
    search: String,
    onContentUpdate: (String) -> Unit
) {
    OutlinedTextField(
        value = search,
        onValueChange = {
            onContentUpdate(it)
        },
        label = { Text(LanguageManager.getState("gui.text.search").value) },
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = {
                onContentUpdate("")
            }) {
                Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
            }
        }
    )
}

@Composable
fun tableHeader(
    indexColumn: Boolean,
    indexColWidth: Dp,
    headerList: List<TableHeader>,
    sortingStates: MutableState<Map<TableHeader, MutableState<SortingState>>>,
    onSortingUpdate: (TableHeader, SortingState) -> Unit
) {
    Card(modifier = Modifier.padding(5.dp).fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(3.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (indexColumn) {
                Row(
                    modifier = Modifier.width(indexColWidth).height(30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            headerList.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f).height(30.dp)
                        .clickable {
                            updateSortingStates(sortingStates, it)
                            onSortingUpdate(it, sortingStates.value[it]!!.value)
                        },
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LanguageManager.getState(it.headerText).value,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold
                    )

                    when (sortingStates.value[it]!!.value) {
                        SortingState.DESC -> {
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = "",
                                modifier = Modifier.width(15.dp)
                            )

                            onSortingUpdate(it, sortingStates.value[it]!!.value)
                        }

                        SortingState.ASC -> {
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = "",
                                modifier = Modifier.width(15.dp)
                            )

                            onSortingUpdate(it, sortingStates.value[it]!!.value)
                        }

                        else -> {
                            // to be left empty
                        }
                    }
                }
            }
        }
    }
}

@Composable
inline fun <reified T> tableContent(
    currentItem: MutableState<T?>,
    tableContent: SnapshotStateList<T>,
    indexColumn: Boolean,
    indexColWidth: Dp,
    noinline onRowSelection: (T) -> Unit
) {
    LazyColumn {
        items(items = tableContent) {
            val rowIndex = tableContent.indexOf(it) + 1
            tableRow(it, indexColumn, rowIndex, indexColWidth, onRowSelection, it == currentItem.value)
        }
    }
}

@Composable
inline fun <reified T> tableRow(
    item: T,
    indexColumn: Boolean,
    rowIndex: Int,
    indexColWidth: Dp,
    noinline onRowSelection: (T) -> Unit,
    selected: Boolean
) {
    val color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.background
    Card(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .clickable {
                onRowSelection(item)
            },
        backgroundColor = color
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (indexColumn) {
                Row(
                    modifier = Modifier.width(indexColWidth),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "$rowIndex")
                }
            }

            val rowContent = item!!::class.members
                .filter { f -> f.annotations.any { a -> a is TableHeader && a.columnIndex >= 0 } }
                .sortedBy { k ->
                    val header = getTableHeader(k.annotations)
                    header.columnIndex
                }
                .map { t -> t.call(item) }
            rowContent.forEach { rc ->
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    if (rc != null) {
                        Text(modifier = Modifier.padding(5.dp), text = "$rc")
                    } else {
                        Text(modifier = Modifier.padding(5.dp), text = "--")
                    }
                }
            }
        }
    }
}