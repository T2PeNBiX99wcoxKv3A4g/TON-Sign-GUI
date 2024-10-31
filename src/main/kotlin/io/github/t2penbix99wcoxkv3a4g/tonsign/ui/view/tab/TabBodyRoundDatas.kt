package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import androidx.navigation.NavHostController
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18nState
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundDatas
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundTypeConvert
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.RoundData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.Terrors
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.WonOrLost
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.playerUrl
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tableView

class TabBodyRoundDatas : TabBodyBase() {
    override val title: String
        get() = "gui.tab.title.round_datas"

    override val id: String
        get() = "round_datas"

    override val maxWidth: Float
        get() = 0.35f

    val roundData: MutableState<RoundData?> = mutableStateOf(null)

    @Composable
    override fun icon() {
        Icon(Icons.Default.Info, contentDescription = title.i18n())
    }

    @Composable
    override fun view(
        navController: NavHostController,
        padding: PaddingValues,
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
            Unknown -> "gui.text.round_datas.unknown".i18n()
            Alive -> "gui.text.round_datas.alive".i18n()
            Death -> "gui.text.round_datas.death".i18n()
            Left -> "gui.text.round_datas.left".i18n()
        }
    }

    @Composable
    override fun detailView(
        navController: NavHostController,
        padding: PaddingValues,
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
        val youDeath by remember { "gui.text.round_datas.you_death".i18nState() }
        val youAlive by remember { "gui.text.round_datas.you_alive".i18nState() }
        val playersText by remember { "gui.text.round_datas.players".i18nState() }
        val terrorText by remember { "gui.text.round_datas.terror".i18nState() }

        SelectionContainer {
            Column(
                Modifier.fillMaxSize().padding(10.dp).verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top
            ) {
                Text(roundDetail.map)
                Text("gui.text.round_datas.round_type".i18n(RoundTypeConvert.getTextOfRound(roundDataGet.roundType)))

                if (roundDetail.isDeath)
                    Text(youDeath)
                else
                    Text(youAlive)

                when (roundDetail.isWon) {
                    WonOrLost.UnKnown -> Text("gui.text.round_datas.unknown".i18n())
                    WonOrLost.Won -> Text("gui.text.round_datas.player_won".i18n())
                    WonOrLost.Lost -> Text("gui.text.round_datas.player_lost".i18n())
                    WonOrLost.Left -> Text("gui.text.round_datas.left".i18n())
                    WonOrLost.InProgress -> Text("gui.text.round_datas.round_is_still_in_progress".i18n())
                }

                if (roundDetail.players.isNotEmpty())
                    Text(playersText)
                Column(Modifier.padding(10.dp)) {
                    roundDetail.players.forEach {
                        Box(
                            Modifier.fillMaxWidth()
                                .background(Color(0, 0, 0, 40))
                                .padding(5.dp)
                        ) {
                            Column {
                                Text(text = buildAnnotatedString {
                                    append("${it.name} - ${playerStatus(it.status)}")
                                    addLink(LinkAnnotation.Url(playerUrl(it)), 0, it.name.length)
                                })

                                if (it.deathMsg != null)
                                    Text(it.deathMsg!!)
                            }
                        }
                    }
                }

                if (terrors.names.isNotEmpty())
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