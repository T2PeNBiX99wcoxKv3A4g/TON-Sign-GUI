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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import androidx.navigation.NavHostController
import io.github.t2penbix99wcoxkv3a4g.tonsign.OSCSender
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.SaveManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18nState
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.switchWithText

class TabBodySetting : TabBodyBase() {
    override val title: String
        get() = "gui.tab.title.setting"

    override val id: String
        get() = "setting"

    @Composable
    override fun icon() {
        Icon(Icons.Default.Settings, contentDescription = title.i18n())
    }

    @Composable
    override fun view(
        navController: NavHostController,
        padding: PaddingValues,
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        var needRefreshSet by needRefresh
        var needRestartSet by needRestart
        var onlySpecial by mutableStateOf(ConfigManager.config.onlySpecial)
        var autoScrollToDown by mutableStateOf(ConfigManager.config.autoScrollToDown)
        var onTop by mutableStateOf(ConfigManager.config.onTop)
        val forceSendTrue by "gui.button.setting.force_send_true".i18nState()
        val forceSendFalse by "gui.button.setting.force_send_false".i18nState()
        val refresh by "gui.button.setting.refresh".i18nState()
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
                    LanguageManager.setLanguage("jp")
                    needRefreshSet = true
                }
            ) {
                Text("日本語")
            }
            Button(
                onClick = {
                    LanguageManager.setLanguage("en")
                    needRefreshSet = true
                }
            ) {
                Text("English")
            }
            Button(
                onClick = {
                    needRefreshSet = true
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
                    SaveManager.save()
                }
            ) {
                Text("Save data")
            }
            switchWithText("Only send notification when round is special", onlySpecial) {
                onlySpecial = it
                ConfigManager.config.onlySpecial = it
            }
            switchWithText("Auto scroll to down in logs", autoScrollToDown) {
                autoScrollToDown = it
                ConfigManager.config.autoScrollToDown = it
            }
            switchWithText("Always On Top", onTop) {
                onTop = it
                ConfigManager.config.onTop = it
                needRestartSet = true
            }
        }
    }
}