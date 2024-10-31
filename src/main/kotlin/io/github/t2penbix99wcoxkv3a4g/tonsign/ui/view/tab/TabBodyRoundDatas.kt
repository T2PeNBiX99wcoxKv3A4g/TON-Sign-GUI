package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundDatas
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundTypeConvert
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.RoundData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.Terrors
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.WonOrLost
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tableView

class TabBodyRoundDatas : TabBodyBase() {
    override val title: String
        get() = "gui.tab.title.round_datas"
    
    override val maxWidth: Float
        get() = 0.35f

    val roundData: MutableState<RoundData?> = mutableStateOf(null)

    @Composable
    override fun view(
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        val roundData: MutableState<RoundData?> = remember { roundData }

        tableView(roundData, roundDatas, onRowSelection = {
            roundData.value = it
        })
    }

    private fun playerStatus(status: PlayerStatus): String {
        return when (status) {
            Unknown -> LanguageManager.get("gui.text.round_datas.unknown")
            Alive -> LanguageManager.get("gui.text.round_datas.alive")
            Death -> LanguageManager.get("gui.text.round_datas.death")
            Left -> LanguageManager.get("gui.text.round_datas.left")
        }
    }

    @Composable
    override fun detailView(
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        val roundData: MutableState<RoundData?> = remember { roundData }
        if (roundData.value == null)
            return

        val roundDataGet = roundData.value!!
        val roundDetail = roundDataGet.roundDetail
        val terrors = Terrors(roundDetail.terrors, roundDataGet.roundType)
        val scrollState = rememberScrollState()
        val youDeath by remember { LanguageManager.getState("gui.text.round_datas.you_death") }
        val youAlive by remember { LanguageManager.getState("gui.text.round_datas.you_alive") }
        val playersText by remember { LanguageManager.getState("gui.text.round_datas.players") }
        val terrorText by remember { LanguageManager.getState("gui.text.round_datas.terror") }

        SelectionContainer {
            Column(
                Modifier.fillMaxSize().padding(10.dp).verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top
            ) {
                Text(roundDetail.map)
                Text(
                    LanguageManager.get(
                        "gui.text.round_datas.round_type",
                        RoundTypeConvert.getTextOfRound(roundDataGet.roundType)
                    )
                )

                if (roundDetail.isDeath)
                    Text(youDeath)
                else
                    Text(youAlive)

                when (roundDetail.isWon) {
                    WonOrLost.UnKnown -> Text(LanguageManager.get("gui.text.round_datas.unknown"))
                    WonOrLost.Won -> Text(LanguageManager.get("gui.text.round_datas.player_won"))
                    WonOrLost.Lost -> Text(LanguageManager.get("gui.text.round_datas.player_lost"))
                    WonOrLost.Left -> Text(LanguageManager.get("gui.text.round_datas.left"))
                    WonOrLost.InProgress -> Text(LanguageManager.get("gui.text.round_datas.round_is_still_in_progress"))
                }

                Text(playersText)
                Column(Modifier.padding(10.dp)) {
                    roundDetail.players.forEach {
                        Box(
                            Modifier.fillMaxWidth()
                                .background(Color(0, 0, 0, 40))
                                .padding(5.dp)
                        ) {
                            Column {
                                Text("${it.name} - ${playerStatus(it.status)}")

                                if (it.deathMsg != null)
                                    Text(it.deathMsg!!)
                            }
                        }
                    }
                }

                Text(terrorText)
                Column(Modifier.padding(10.dp)) {
                    terrors.names.forEach {
                        Box(
                            Modifier.fillMaxWidth()
                                .background(Color(0, 0, 0, 40))
                                .padding(5.dp)
                        ) {
                            Text(it)
                        }
                    }
                }
            }
        }
    }
}