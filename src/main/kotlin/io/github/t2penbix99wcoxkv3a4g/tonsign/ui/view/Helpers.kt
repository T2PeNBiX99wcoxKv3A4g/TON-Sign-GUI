@file:Suppress("unused")

package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyBase
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyLogs
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyMain
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyRoundDatas
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodySetting
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.TabBodyVRChatLogs

enum class SelectionState(val gui: TabBodyBase) {
    Main(TabBodyMain()),
    RoundDatas(TabBodyRoundDatas()),
    Logs(TabBodyLogs()),
    VRChatLogs(TabBodyVRChatLogs()),
    Setting(TabBodySetting())
}

enum class SortingState {
    ASC, DESC, NONE
}

@Composable
fun textBox(text: String = "Item") {
    Box(
        Modifier.fillMaxWidth()
            .background(Color(0, 0, 0, 40))
            .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text)
    }
}

@Composable
fun textBoxWithLink(
    text: String = "Item",
    link: String = "https://vrchat.com",
    start: Int = 0,
    end: Int = text.length
) {
    Box(
        Modifier.fillMaxWidth()
            .background(Color(0, 0, 0, 40))
            .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = buildAnnotatedString {
            append(text)
            addLink(LinkAnnotation.Url(link), start, end)
        })
    }
}

@Composable
fun textBoxWithLink(text: String = "Item", link: String = "https://vrchat.com", linkString: String) =
    textBoxWithLink(text, link, text.indexOf(linkString), text.lastIndexOf(linkString))

@Suppress("unused")
@Composable
fun textBox(text: AnnotatedString) {
    Box(
        Modifier.height(32.dp)
            .fillMaxWidth()
            .background(Color(0, 0, 0, 20))
            .padding(start = 10.dp, end = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text)
    }
}

@Composable
fun switchWithText(text: String, checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?) {
    Row(
        Modifier
            .width(320.dp)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.weight(1f, true))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

fun updateSortingStates(
    sortingStates: MutableState<Map<TableHeader, MutableState<SortingState>>>,
    tableHeader: TableHeader
) {
    val newMap = sortingStates.value
    when (sortingStates.value[tableHeader]!!.value) {
        SortingState.ASC -> newMap[tableHeader]!!.value = SortingState.DESC
        SortingState.DESC -> newMap[tableHeader]!!.value = SortingState.NONE
        SortingState.NONE -> newMap[tableHeader]!!.value = SortingState.ASC
    }
    sortingStates.value.filter { it.key != tableHeader }.forEach {
        newMap[it.key]!!.value = SortingState.NONE
    }

    sortingStates.value = newMap
}

fun playerUrl(player: PlayerData): String {
    return "https://vrchat.com/home/user/${player.id}"
}

fun playerUrl(player: String): String {
    return "https://vrchat.com/home/user/${player}"
}

fun worldUrl(world: String): String {
    return "https://vrchat.com/home/world/${world}"
}

inline fun <reified T : Any> sort(t: T, tableHeader: TableHeader): String {
    val filterCriterion = t::class.members.first { f -> f.annotations.any { a -> a == tableHeader } }
    val call = filterCriterion.call(t)
    return if (call != null) {
        "$call"
    } else {
        ""
    }
}