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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.api.ApiClient
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.l10n
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.l10nState
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.labeledCheckbox
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.loginField
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.passwordField

class TabBodyLogin : TabBodyBase() {
    override val title: String
        get() = "gui.tab.title.login"
    override val id: String
        get() = "login"
    override val enabled: Boolean
        get() = Utils.logger.isDebugEnabled()

    @Composable
    override fun icon() {
        Icon(Icons.Default.AccountCircle, contentDescription = title.l10n())
    }
    
    private val apiClient = ApiClient()

    @Composable
    override fun view(navController: NavHostController, padding: PaddingValues) {
        val scrollState = rememberScrollState()
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var rememberMe by remember { mutableStateOf(false) }

        val usernameText by "gui.text.login.username".l10nState()
        val enterUserNameText by "gui.text.login.enter_username".l10nState()
        val passwordText by "gui.text.login.password".l10nState()
        val enterPasswordText by "gui.text.login.enter_password".l10nState()
        val rememberMeText by "gui.text.login.remember_me".l10nState()
        val loginText by "gui.text.login.login".l10nState()

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
                label = { Text(usernameText) },
                placeholder = { Text(enterUserNameText) }
            )
            passwordField(
                value = password,
                onValueChange = { password = it },
                submit = {},
                label = { Text(passwordText) },
                placeholder = { Text(enterPasswordText) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            labeledCheckbox(
                label = rememberMeText,
                onCheckChanged = { rememberMe = !rememberMe },
                onCheckedChange = {
                    rememberMe = it
                },
                isChecked = rememberMe
            )
            Button(
                onClick = { apiClient.login(username, password) },
                enabled = true,
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(loginText)
            }
        }
    }
}