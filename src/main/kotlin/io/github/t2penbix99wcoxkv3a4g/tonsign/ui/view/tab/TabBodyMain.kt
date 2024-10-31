package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import androidx.navigation.NavHostController
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18nState
import io.github.t2penbix99wcoxkv3a4g.tonsign.players
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.nextPrediction
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.playerUrl
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.textBox
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.textBoxWithLink
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.VRChatWatcher

class TabBodyMain : TabBodyBase() {
    override val title: String
        get() = "gui.tab.title.main"

    override val id: String
        get() = "main"

    @Composable
    override fun icon() {
        Icon(Icons.Default.Home, contentDescription = title.i18n())
    }

    @Composable
    override fun view(
        navController: NavHostController,
        padding: PaddingValues,
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        var isWaitingVRChat by VRChatWatcher.isWaitingVRChat
        var nextPredictionSet by nextPrediction
        val isNotRunning by remember { "gui.text.main.vrchat_is_not_running".i18nState() }
        val roundSpecial by remember { "gui.text.main.round_special".i18nState() }
        val roundClassic by remember { "gui.text.main.round_classic".i18nState() }
        val waitingJoinTon by remember { "gui.text.main.waiting_join_ton".i18nState() }
        val theIsNothingHere by remember { "gui.text.main.the_is_nothing_here".i18nState() }
        val playersText by remember { "gui.text.round_datas.players".i18nState() }
        val players = remember { players }
        val scrollState = rememberScrollState()

        SelectionContainer {
            Column(
                modifier = Modifier.padding(10.dp)
                    .verticalScroll(scrollState)
            ) {
                if (isWaitingVRChat)
                    textBox(isNotRunning)
                else if (logWatcher != null) {
                    if (logWatcher!!.isInTON.value) {
                        val special = roundSpecial
                        val classic = roundClassic

                        if (nextPredictionSet == GuessRoundType.Special || nextPredictionSet == GuessRoundType.Classic)
                            textBox("gui.text.main.next_prediction".i18n(if (nextPredictionSet == GuessRoundType.Special) special else classic))

                        val recentRounds by remember { logWatcher!!.getRecentRoundsLogState }

                        if (!recentRounds.isBlank()) {
                            textBox("gui.text.main.recent_rounds".i18n(recentRounds))
                        } else {
                            textBox(theIsNothingHere)
                        }
                    } else {
                        textBox(waitingJoinTon)
                    }
                    if (players.isNotEmpty())
                        textBox(playersText)
                    Column(Modifier.padding(10.dp)) {
                        players.forEach {
                            textBoxWithLink(it.name, playerUrl(it))
                        }
                    }
                }
            }
        }
    }
}
