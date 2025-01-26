package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.swapList
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.l10nState
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.getTextOfRound
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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

@Suppress("unused")
@Composable
inline fun <reified T : Any> tableView(
    currentItem: MutableState<T?>,
    content: SnapshotStateList<T>,
    searchButtons: List<SearchButton>,
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

    val search = remember { mutableStateOf("") }
    val tabContent = remember { mutableStateListOf<T>() }

    tabContent.swapList(content)

    if (search.value.isNotEmpty())
        searchFilter(tabContent, fields, search)

    val onContentUpdate: (String) -> Unit = {
        search.value = it
        searchFilter(tabContent, fields, search)
    }

    Column(
        Modifier.fillMaxWidth()
    ) {
        searchField(search.value, onContentUpdate, searchButtons)

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
inline fun <reified T : Any> tableView(
    currentItem: MutableState<T?>,
    content: SnapshotStateList<T>,
    searchButtons: List<SearchButton>,
    indexColumn: Boolean = false,
    indexColWidth: Dp = 30.dp,
    filter: List<TableValue>,
    noinline onRowSelection: (T) -> Unit
) {
    @Suppress("NAME_SHADOWING")
    val filter by remember { mutableStateOf(filter) }
    val fields = T::class.members.filter {
        filter.any { a -> a.name == it.name }
    }.sortedBy {
        val value = filter.find { a -> a.name == it.name }
        value?.columnIndex ?: 0
    }

    val search = remember { mutableStateOf("") }
    val tabContent = remember { mutableStateListOf<T>() }

    tabContent.swapList(content)

    if (search.value.isNotEmpty())
        searchFilter(tabContent, fields, search)

    val onContentUpdate: (String) -> Unit = {
        search.value = it
        searchFilter(tabContent, fields, search)
    }

    Column(
        Modifier.fillMaxWidth()
    ) {
        searchField(search.value, onContentUpdate, searchButtons)

        val filterList = filter
            .filter { it.columnIndex >= 0 }

        val stateMap = filterList.associateWith { mutableStateOf(SortingState.NONE) }
        val sortingStates = remember { mutableStateOf(stateMap) }

        val onSortingUpdate: (TableValue, SortingState) -> Unit = { tableValue, sortingState ->
            val sortedList: List<T> = when (sortingState) {
                SortingState.ASC -> {
                    tabContent.sortedBy { t ->
                        sort(t, tableValue)
                    }
                }

                SortingState.DESC -> {
                    tabContent.sortedByDescending { t ->
                        sort(t, tableValue)
                    }
                }

                else -> {
                    tabContent
                }
            }

            tabContent.swapList(sortedList)
        }

        tableValue(indexColumn, indexColWidth, filterList, sortingStates, onSortingUpdate)
        tableContent(currentItem, tabContent, indexColumn, indexColWidth, filterList, onRowSelection)
    }
}

@Composable
inline fun <reified T : Any> tableView(
    currentItem: MutableState<T?>,
    content: SnapshotStateMap<*, T>,
    searchButtons: List<SearchButton>,
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

    val search = remember { mutableStateOf("") }
    val tabContent = remember { mutableStateListOf<T>() }

    tabContent.swapList(content)

    if (search.value.isNotEmpty())
        searchFilter(tabContent, fields, search)

    val onContentUpdate: (String) -> Unit = {
        search.value = it
        searchFilter(tabContent, fields, search)
    }

    Column(
        Modifier.fillMaxWidth()
    ) {
        searchField(search.value, onContentUpdate, searchButtons)

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

@Suppress("unused")
@Composable
fun searchField(
    search: String,
    onContentUpdate: (String) -> Unit
) {
    val searchText by "gui.text.search".l10nState()

    OutlinedTextField(
        value = search,
        onValueChange = {
            onContentUpdate(it)
        },
        label = { Text(searchText) },
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            if (search.isNotEmpty()) {
                IconButton(onClick = {
                    onContentUpdate("")
                }) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                }
            }
        }
    )
}

@Suppress("unused")
@Composable
fun searchField(
    search: String,
    onContentUpdate: (String) -> Unit,
    onBottomDo: () -> Unit
) {
    val searchText by "gui.text.search".l10nState()

    OutlinedTextField(
        value = search,
        onValueChange = {
            onContentUpdate(it)
        },
        label = { Text(searchText) },
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            Row {
                if (search.isNotEmpty()) {
                    IconButton(onClick = {
                        onContentUpdate("")
                    }) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                    }
                }
                IconButton(onClick = {
                    onBottomDo()
                }) {
                    Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Bottom")
                }
            }
        }
    )
}

data class SearchButton(
    val onClick: () -> Unit,
    val imageVector: ImageVector,
    val contentDescription: String
)

@Composable
fun searchField(
    search: String,
    onContentUpdate: (String) -> Unit,
    onBottomDo: () -> Unit,
    searchButtons: List<SearchButton>
) {
    val searchText by "gui.text.search".l10nState()

    OutlinedTextField(
        value = search,
        onValueChange = {
            onContentUpdate(it)
        },
        label = { Text(searchText) },
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            Row {
                if (search.isNotEmpty()) {
                    IconButton(onClick = {
                        onContentUpdate("")
                    }) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                    }
                }
                IconButton(onClick = {
                    onBottomDo()
                }) {
                    Icon(imageVector = Icons.Filled.VerticalAlignBottom, contentDescription = "Go To Bottom")
                }
                searchButtons.forEach {
                    IconButton(onClick = {
                        it.onClick()
                    }) {
                        Icon(imageVector = it.imageVector, contentDescription = it.contentDescription)
                    }
                }
            }
        }
    )
}

@Composable
fun searchField(
    search: String,
    onContentUpdate: (String) -> Unit,
    searchButtons: List<SearchButton>
) {
    val searchText by "gui.text.search".l10nState()

    OutlinedTextField(
        value = search,
        onValueChange = {
            onContentUpdate(it)
        },
        label = { Text(searchText) },
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            Row {
                if (search.isNotEmpty()) {
                    IconButton(onClick = {
                        onContentUpdate("")
                    }) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                    }
                }
                searchButtons.forEach {
                    IconButton(onClick = {
                        it.onClick()
                    }) {
                        Icon(imageVector = it.imageVector, contentDescription = it.contentDescription)
                    }
                }
            }
        }
    )
}

@Composable
fun tableValue(
    indexColumn: Boolean,
    indexColWidth: Dp,
    filter: List<TableValue>,
    sortingStates: MutableState<Map<TableValue, MutableState<SortingState>>>,
    onSortingUpdate: (TableValue, SortingState) -> Unit
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
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            filter.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f).height(30.dp)
                        .clickable {
                            updateSortingStates(sortingStates, it)
                            onSortingUpdate(it, sortingStates.value[it]!!.value)
                        },
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val headerText by it.headerText.l10nState()

                    Text(
                        text = headerText,
                        style = MaterialTheme.typography.labelLarge,
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
                        style = MaterialTheme.typography.labelLarge,
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
                    val headerText by it.headerText.l10nState()

                    Text(
                        text = headerText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    when (sortingStates.value[it]?.value) {
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
inline fun <reified T> tableContent(
    currentItem: MutableState<T?>,
    tableContent: SnapshotStateList<T>,
    indexColumn: Boolean,
    indexColWidth: Dp,
    filter: List<TableValue>,
    noinline onRowSelection: (T) -> Unit
) {
    LazyColumn {
        items(items = tableContent) {
            val rowIndex = tableContent.indexOf(it) + 1
            tableRow(it, indexColumn, rowIndex, indexColWidth, onRowSelection, it == currentItem.value, filter)
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
    val color = if (selected) CardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.surfaceContainer,
        disabledContainerColor = MaterialTheme.colorScheme.surface
    ) else CardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary,
        disabledContentColor = MaterialTheme.colorScheme.surfaceContainer,
        disabledContainerColor = MaterialTheme.colorScheme.surface
    )
    Card(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .clickable {
                onRowSelection(item)
            },
        colors = color
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

            @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
            val headerList = item!!::class.members
                .flatMap { it.annotations }
                .filter { it is TableHeader && it.columnIndex >= 0 }
                .sortedBy {
                    if (it !is TableHeader) return@sortedBy -1
                    it.columnIndex
                }
                .map { it as TableHeader }

            var i = 0

            rowContent.forEach { rc ->
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    if (rc != null && i < headerList.size) {
                        val header = headerList[i]

                        when {
                            header.isTime && rc is Long -> {
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                val zone = Instant.ofEpochSecond(rc).atZone(ZoneId.systemDefault())
                                Text(modifier = Modifier.padding(5.dp), text = zone.format(formatter))
                            }

                            rc is RoundType -> {
                                Text(modifier = Modifier.padding(5.dp), text = rc.getTextOfRound())
                            }

                            else -> Text(modifier = Modifier.padding(5.dp), text = "$rc")
                        }
                    } else {
                        Text(modifier = Modifier.padding(5.dp), text = "--")
                    }
                }
                i++
            }
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
    selected: Boolean,
    filters: List<TableValue>
) {
    val color = if (selected) CardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.surfaceContainer,
        disabledContainerColor = MaterialTheme.colorScheme.surface
    ) else CardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary,
        disabledContentColor = MaterialTheme.colorScheme.surfaceContainer,
        disabledContainerColor = MaterialTheme.colorScheme.surface
    )
    Card(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .clickable {
                onRowSelection(item)
            },
        colors = color
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
                .filter { filters.any { a -> a.name == it.name } }
                .sortedBy { k ->
                    val value = filters.find { a -> a.name == k.name }
                    value?.columnIndex ?: 0
                }
                .map { t -> t.call(item) }

            var i = 0

            rowContent.forEach { rc ->
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    if (rc != null && i < filters.size) {
                        val filter = filters[i]

                        when {
                            filter.isTime && rc is Long -> {
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                val zone = Instant.ofEpochSecond(rc).atZone(ZoneId.systemDefault())
                                Text(modifier = Modifier.padding(5.dp), text = zone.format(formatter))
                            }

                            rc is RoundType -> {
                                Text(modifier = Modifier.padding(5.dp), text = rc.getTextOfRound())
                            }

                            else -> Text(modifier = Modifier.padding(5.dp), text = "$rc")
                        }
                    } else {
                        Text(modifier = Modifier.padding(5.dp), text = "--")
                    }
                }
                i++
            }
        }
    }
}