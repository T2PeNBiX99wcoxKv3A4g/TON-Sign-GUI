package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import io.github.t2penbix99wcoxkv3a4g.tonsign.OSCSender
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.switchWithText

class TabBodySetting : TabBodyBase() {
    override val title = { "gui.tab.title.setting" }

    @Composable
    override fun view(
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        var needRefreshSet by needRefresh
        var onlySpecial by remember { mutableStateOf(ConfigManager.config.onlySpecial) }
        var autoScrollToDown by remember { mutableStateOf(ConfigManager.config.autoScrollToDown) }
        val forceSendTrue by remember { LanguageManager.getState("gui.button.force_send_true") }
        val forceSendFalse by remember { LanguageManager.getState("gui.button.force_send_false") }
        val refresh by remember { LanguageManager.getState("gui.button.refresh") }
        val state = rememberScrollState()

        Column(
            modifier = Modifier.padding(10.dp)
                .verticalScroll(state)
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
                    LanguageManager.setLang("jp")
                    needRefreshSet = true
                }
            ) {
                Text("日本語")
            }
            Button(
                onClick = {
                    LanguageManager.setLang("en")
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
            switchWithText("Only send notification when round is special", onlySpecial) {
                onlySpecial = it
                ConfigManager.config.onlySpecial = it
            }
            switchWithText("Auto scroll to down in logs", autoScrollToDown) {
                autoScrollToDown = it
                ConfigManager.config.autoScrollToDown = it
            }
        }
    }
}