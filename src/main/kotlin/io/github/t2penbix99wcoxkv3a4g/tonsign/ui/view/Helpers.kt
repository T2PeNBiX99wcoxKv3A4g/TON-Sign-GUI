@file:Suppress("unused")

package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.*

enum class SelectionState(val gui: TabBodyBase) {
    Main(TabBodyMain()),
    RoundDatas(TabBodyRoundDatas()),
    Logs(TabBodyLogs()),
    VRChatLogs(TabBodyVRChatLogs()),
    Setting(TabBodySetting()),
    Login(TabBodyLogin())
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
        Modifier.fillMaxWidth()
            .background(Color(0, 0, 0, 40))
            .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text)
    }
}

@Composable
fun switchWithText(text: String, checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?) {
    Row(
        Modifier
            .width(520.dp)
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

fun updateSortingStates(
    sortingStates: MutableState<Map<TableValue, MutableState<SortingState>>>,
    tableValue: TableValue
) {
    val newMap = sortingStates.value
    when (sortingStates.value[tableValue]!!.value) {
        SortingState.ASC -> newMap[tableValue]!!.value = SortingState.DESC
        SortingState.DESC -> newMap[tableValue]!!.value = SortingState.NONE
        SortingState.NONE -> newMap[tableValue]!!.value = SortingState.ASC
    }
    sortingStates.value.filter { it.key != tableValue }.forEach {
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

inline fun <reified T : Any> sort(t: T, tableValue: TableValue): String {
    val filterCriterion = t::class.members.first { f -> f.name == tableValue.name }
    val call = filterCriterion.call(t)
    return if (call != null) {
        "$call"
    } else {
        ""
    }
}

@Composable
fun loginField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = @Composable { Text("Login") },
    placeholder: @Composable (() -> Unit)? = @Composable { Text("Enter your Login") }
) {
    var isVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        leadingIcon = {
            Icon(
                Icons.Default.Person,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(
                    if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        placeholder = placeholder,
        label = label,
        singleLine = true,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
fun passwordField(
    value: String,
    onValueChange: (String) -> Unit,
    submit: () -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = @Composable { Text("Password") },
    placeholder: @Composable (() -> Unit)? = @Composable { Text("Enter your Password") }
) {
    var isVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        leadingIcon = {
            Icon(
                Icons.Default.Key,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(
                    if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = { submit() }
        ),
        placeholder = placeholder,
        label = label,
        singleLine = true,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
fun labeledCheckbox(
    label: String,
    onCheckChanged: () -> Unit,
    onCheckedChange: ((Boolean) -> Unit)?,
    isChecked: Boolean
) {
    Row(
        Modifier
            .clickable(
                onClick = onCheckChanged
            )
            .padding(4.dp)
    ) {
        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
        Spacer(Modifier.size(6.dp))
        Text(label)
    }
}