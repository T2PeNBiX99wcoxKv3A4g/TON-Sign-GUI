package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import androidx.navigation.NavHostController
import io.github.t2penbix99wcoxkv3a4g.tonsign.api.ApiClient
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.labeledCheckbox
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.loginField
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
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var rememberMe by remember { mutableStateOf(false) }
        val api = ApiClient()

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 30.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            loginField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                placeholder = { Text("Enter your Username") }
            )
            passwordField(
                value = password,
                onValueChange = { password = it },
                submit = {},
            )
            Spacer(modifier = Modifier.height(10.dp))
            labeledCheckbox(
                label = "Remember Me",
                onCheckChanged = {},
                onCheckedChange = {
                    rememberMe = it
                },
                isChecked = rememberMe
            )
            Button(
                onClick = { api.login(username, password) },
                enabled = true,
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("Login")
            }
        }
    }
}