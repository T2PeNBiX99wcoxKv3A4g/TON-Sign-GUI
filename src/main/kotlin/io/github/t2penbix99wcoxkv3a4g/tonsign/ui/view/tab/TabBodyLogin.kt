package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import androidx.navigation.NavHostController
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.passwordField

class TabBodyLogin : TabBodyBase() {
    override val title: String
        get() = "Login"
    override val id: String
        get() = "login"

    @Composable
    override fun icon() {
        Icon(Icons.Default.AccountBox, contentDescription = title.i18n())
    }

    @Composable
    override fun view(
        navController: NavHostController,
        padding: PaddingValues,
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        val scrollState = rememberScrollState()
        var password by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.padding(10.dp)
                .verticalScroll(scrollState)
        ) {
            passwordField(
                value = password,
                onValueChange = {password = it},
                submit = {},
                leadingIcon = {
                    Icon(
                        Icons.Default.Key,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}