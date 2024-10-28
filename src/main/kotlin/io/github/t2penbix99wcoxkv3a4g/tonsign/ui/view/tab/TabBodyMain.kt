package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.nextPrediction
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.textBox
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.VRChatWatcher

class TabBodyMain : TabBodyBase() {
    override val title = { "gui.tab.title.main" }

    @Composable
    override fun view(
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        var isWaitingVRChat by VRChatWatcher.isWaitingVRChat
        var nextPredictionSet by nextPrediction
        val isNotRunning by remember { LanguageManager.getState("gui.text.vrchat_is_not_running") }
        val roundSpecial by remember { LanguageManager.getState("gui.text.round_special") }
        val roundClassic by remember { LanguageManager.getState("gui.text.round_classic") }
        val waitingJoinTon by remember { LanguageManager.getState("gui.text.waiting_join_ton") }
        val theIsNothingHere by remember { LanguageManager.getState("gui.text.the_is_nothing_here") }

        SelectionContainer {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                if (isWaitingVRChat)
                    textBox(isNotRunning)
                else if (logWatcher != null) {
                    if (logWatcher!!.isInTON.value) {
                        val special = roundSpecial
                        val classic = roundClassic

                        if (nextPredictionSet == GuessRoundType.Special || nextPredictionSet == GuessRoundType.Classic) {
                            val nextPrediction by remember {
                                LanguageManager.getState(
                                    "gui.text.next_prediction",
                                    if (nextPredictionSet == GuessRoundType.Special) special else classic
                                )
                            }

                            textBox(nextPrediction)
                        }

                        val recentRounds = logWatcher!!.getRecentRoundsLogState.value

                        if (!recentRounds.isBlank()) {
                            val recentRounds by remember {
                                LanguageManager.getState(
                                    "gui.text.recent_rounds",
                                    recentRounds
                                )
                            }
                            textBox(recentRounds)
                        } else {
                            textBox(theIsNothingHere)
                        }
                    } else {
                        textBox(waitingJoinTon)
                    }
                }
            }
        }
    }
}
