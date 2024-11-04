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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import androidx.navigation.NavHostController
import io.github.t2penbix99wcoxkv3a4g.tonsign.delayToLoadingLog
import io.github.t2penbix99wcoxkv3a4g.tonsign.isInWorld
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18nState
import io.github.t2penbix99wcoxkv3a4g.tonsign.nowWorldID
import io.github.t2penbix99wcoxkv3a4g.tonsign.players
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.nextPrediction
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.playerUrl
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.textBox
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.textBoxWithLink
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.worldUrl
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
        var delayToLoadingLog by delayToLoadingLog
        val isNotRunning by "gui.text.main.vrchat_is_not_running".i18nState()
        val roundSpecial by "gui.text.main.round_special".i18nState()
        val roundClassic by "gui.text.main.round_classic".i18nState()
        val waitingJoinTon by "gui.text.main.waiting_join_ton".i18nState()
        val theIsNothingHere by "gui.text.main.the_is_nothing_here".i18nState()
        val waitUntilJoin by "log.wait_until_join_game".i18nState()
        val nowWorldId by nowWorldID
        val isInWorld by isInWorld
        val scrollState = rememberScrollState()

        SelectionContainer {
            Column(
                modifier = Modifier.padding(10.dp)
                    .verticalScroll(scrollState)
            ) {
                when {
                    isWaitingVRChat -> textBox(isNotRunning)
                    delayToLoadingLog -> textBox(waitUntilJoin)
                    logWatcher != null -> {
                        val logWatcher = logWatcher!!
                        val isInTON by logWatcher.isInTON
                        
                        if (isInTON) {
                            val special = roundSpecial
                            val classic = roundClassic

                            if (nextPredictionSet == GuessRoundType.Special || nextPredictionSet == GuessRoundType.Classic) {
                                val nextPrediction by "gui.text.main.next_prediction".i18nState(if (nextPredictionSet == GuessRoundType.Special) special else classic)
                                textBox(nextPrediction)
                            }

                            val recentRounds by logWatcher.getRecentRoundsLogState

                            if (!recentRounds.isBlank()) {
                                textBox("gui.text.main.recent_rounds".i18n(recentRounds))
                            } else {
                                textBox(theIsNothingHere)
                            }
                        } else {
                            textBox(waitingJoinTon)
                        }
                        if (isInWorld && nowWorldId.isNotEmpty())
                            textBoxWithLink("gui.text.main.current_world".i18n(nowWorldId), worldUrl(nowWorldId))
                        if (players.isNotEmpty())
                            textBox("gui.text.main.players".i18n(players.size))
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
}
