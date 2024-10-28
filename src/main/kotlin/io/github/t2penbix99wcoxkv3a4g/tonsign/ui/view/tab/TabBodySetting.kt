package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import io.github.t2penbix99wcoxkv3a4g.tonsign.OSCSender
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager

class TabBodySetting: TabBodyBase() {
    override val title = { LanguageManager.get("gui.tab.title.setting") }

    @Composable
    override fun view(
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        var needRefreshSet by needRefresh
        
        Column(
            modifier = Modifier.padding(50.dp)
        ) {
            Button(
                onClick = {
                    OSCSender.send(true)
                }
            ) {
                Text(LanguageManager.getState("gui.button.force_send_true").value)
            }
            Button(
                onClick = {
                    OSCSender.send(false)
                }
            ) {
                Text(LanguageManager.getState("gui.button.force_send_false").value)
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
                Text(LanguageManager.getState("gui.button.refresh").value)
            }
        }
    }
}