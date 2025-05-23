package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.t2penbix99wcoxkv3a4g.tonsign.OSCSender
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.needRefresh
import io.github.t2penbix99wcoxkv3a4g.tonsign.needRestart
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.switchWithText

class TabBodySetting : TabBodyBase() {
    override val title: String
        get() = "gui.tab.title.setting"

    override val id: String
        get() = "setting"

    @Composable
    override fun icon() {
        Icon(Icons.Default.Settings, contentDescription = title.l10n())
    }

    // TODO: Remake
    @Composable
    override fun view(navController: NavHostController, padding: PaddingValues) {
        var needRefresh by remember { needRefresh }
        var needRestart by remember { needRestart }
        var roundNotify by remember { mutableStateOf(ConfigManager.config.roundNotify) }
        var roundNotifyOnlySpecial by remember { mutableStateOf(ConfigManager.config.roundNotifyOnlySpecial) }
        var playerJoinedNotify by remember { mutableStateOf(ConfigManager.config.playerJoinedNotify) }
        var playerLeftNotify by remember { mutableStateOf(ConfigManager.config.playerLeftNotify) }
        var autoScrollToDown by remember { mutableStateOf(ConfigManager.config.autoScrollToDown) }
        var onTop by remember { mutableStateOf(ConfigManager.config.onTop) }
        var automaticTurnOnSign by remember { mutableStateOf(ConfigManager.config.automaticTurnOnSign) }
        val forceSendTrue by "gui.button.setting.force_send_true".l10nState()
        val forceSendFalse by "gui.button.setting.force_send_false".l10nState()
        val forceSendTabunTrue by "gui.button.setting.force_send_tabun_true".l10nState()
        val forceSendTabunFalse by "gui.button.setting.force_send_tabun_false".l10nState()
        val refresh by "gui.button.setting.refresh".l10nState()
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.padding(10.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Button(
                onClick = {
                    OSCSender.send(true)
                }
            ) {
                Text(forceSendTrue)
            }
            Button(
                onClick = {
                    OSCSender.send(false)
                }
            ) {
                Text(forceSendFalse)
            }
            Button(
                onClick = {
                    OSCSender.sendTabun(true)
                }
            ) {
                Text(forceSendTabunTrue)
            }
            Button(
                onClick = {
                    OSCSender.sendTabun(false)
                }
            ) {
                Text(forceSendTabunFalse)
            }
            Button(
                onClick = {
                    LocalizationManager.setLanguage("jp")
                    needRefresh = true
                }
            ) {
                Text("日本語")
            }
            Button(
                onClick = {
                    LocalizationManager.setLanguage("en")
                    needRefresh = true
                }
            ) {
                Text("English")
            }
            Button(
                onClick = {
                    needRefresh = true
                }
            ) {
                Text(refresh)
            }
            Button(
                onClick = {
                    ConfigManager.save()
                }
            ) {
                Text("Save config")
            }
            Button(
                onClick = {
                    SecretsManager.save()
                }
            ) {
                Text("Save secrets")
            }
            switchWithText("Send next round notification when round is over", roundNotify) {
                roundNotify = it
                ConfigManager.config.roundNotify = it
            }
            if (roundNotify) {
                switchWithText("Only send notification when round is special", roundNotifyOnlySpecial) {
                    roundNotifyOnlySpecial = it
                    ConfigManager.config.roundNotifyOnlySpecial = it
                }
            }
            switchWithText("Send player joined notification", playerJoinedNotify) {
                playerJoinedNotify = it
                ConfigManager.config.playerJoinedNotify = it
            }
            switchWithText("Send player left notification", playerLeftNotify) {
                playerLeftNotify = it
                ConfigManager.config.playerLeftNotify = it
            }
            switchWithText("Auto scroll to down in logs", autoScrollToDown) {
                autoScrollToDown = it
                ConfigManager.config.autoScrollToDown = it
            }
            switchWithText("Automatic Turn On Sign", automaticTurnOnSign) {
                automaticTurnOnSign = it
                ConfigManager.config.automaticTurnOnSign = it
            }
            switchWithText("Always On Top", onTop) {
                onTop = it
                ConfigManager.config.onTop = it
                needRestart = true
            }
        }
    }
}