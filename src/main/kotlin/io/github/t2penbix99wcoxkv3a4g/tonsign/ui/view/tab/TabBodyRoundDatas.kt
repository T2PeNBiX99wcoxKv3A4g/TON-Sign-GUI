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
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.rememberWindowState
import androidx.navigation.NavHostController
import io.github.t2penbix99wcoxkv3a4g.tonsign.RoundTimerID
import io.github.t2penbix99wcoxkv3a4g.tonsign.isInWorld
import io.github.t2penbix99wcoxkv3a4g.tonsign.lastTime
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.TimerManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18nState
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundDatas
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundTypeConvert
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.RoundData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.Terrors
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.WonOrLost
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.CupcakeEXTheme
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.playerUrl
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.tray.TrayItem
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.tray.TrayRoundDatas
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tableView
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class TabBodyRoundDatas : TabBodyBase() {
    override val title: String
        get() = "gui.tab.title.round_datas"

    override val id: String
        get() = "round_datas"

    override val isOnTop: MutableState<Boolean>
        get() = internalIsOnTop

    override val trays: List<TrayItem>
        get() = listOf(TrayRoundDatas())

    override val maxWidth: Float
        get() = 0.35f

    internal val internalIsOnTop = mutableStateOf(false)

    val roundData: MutableState<RoundData?> = mutableStateOf(null)

    @Composable
    override fun icon() {
        Icon(Icons.Default.History, contentDescription = title.i18n())
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
        val unknown by "gui.text.round_datas.unknown".i18nState()
        val alive by "gui.text.round_datas.alive".i18nState()
        val death by "gui.text.round_datas.death".i18nState()
        val left by "gui.text.round_datas.left".i18nState()

        return when (status) {
            PlayerStatus.Unknown -> unknown
            PlayerStatus.Alive -> alive
            PlayerStatus.Death -> death
            PlayerStatus.Left -> left
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
        val youDeath by "gui.text.round_datas.you_death".i18nState()
        val youAlive by "gui.text.round_datas.you_alive".i18nState()

        val textTime by mutableStateOf(roundDetail.time)
        val realTime by mutableStateOf(TimerManager.get(RoundTimerID))
        val time = if (textTime < 0) realTime else textTime
        val duration = time.toDuration(DurationUnit.MILLISECONDS)
        val hours = duration.inWholeHours
        val minutes = duration.minus(hours.toDuration(DurationUnit.HOURS)).inWholeMinutes
        val seconds = duration.minus(hours.toDuration(DurationUnit.HOURS))
            .minus(minutes.toDuration(DurationUnit.MINUTES)).inWholeSeconds
        val milliSeconds =
            duration.minus(hours.toDuration(DurationUnit.HOURS)).minus(minutes.toDuration(DurationUnit.MINUTES))
                .minus(seconds.toDuration(DurationUnit.SECONDS)).inWholeMilliseconds
        val hoursText = hours.toString().padStart(2, '0')
        val minutesText = minutes.toString().padStart(2, '0')
        val secondsText = seconds.toString().padStart(2, '0')
        val milliSecondsText = milliSeconds.toString().padStart(2, '0')

        val playerTime by mutableStateOf(roundDetail.playerTime)
        val playerDuration = playerTime.toDuration(DurationUnit.MILLISECONDS)
        val playerHours = playerDuration.inWholeHours
        val playerMinutes = playerDuration.minus(playerHours.toDuration(DurationUnit.HOURS)).inWholeMinutes
        val playerSeconds = playerDuration.minus(playerHours.toDuration(DurationUnit.HOURS))
            .minus(playerMinutes.toDuration(DurationUnit.MINUTES)).inWholeSeconds
        val playerMilliSeconds = playerDuration.minus(playerHours.toDuration(DurationUnit.HOURS))
            .minus(playerMinutes.toDuration(DurationUnit.MINUTES))
            .minus(playerSeconds.toDuration(DurationUnit.SECONDS)).inWholeMilliseconds
        val playerHoursText = playerHours.toString().padStart(2, '0')
        val playerMinutesText = playerMinutes.toString().padStart(2, '0')
        val playerSecondsText = playerSeconds.toString().padStart(2, '0')
        val playerMilliSecondsText = playerMilliSeconds.toString().padStart(2, '0')

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

                if (playerTime > 0)
                    Text("$playerHoursText:$playerMinutesText:$playerSecondsText.$playerMilliSecondsText / $hoursText:$minutesText:$secondsText.$milliSecondsText")
                else
                    Text("$hoursText:$minutesText:$secondsText.$milliSecondsText")

                if (terrors.names.isNotEmpty())
                    Text("gui.text.round_datas.terrors".i18n(terrors.names.size))
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

                if (roundDetail.players.isNotEmpty())
                    Text(
                        "gui.text.round_datas.players".i18n(
                            roundDetail.players.filter { it.status == PlayerStatus.Alive }.size,
                            roundDetail.players.size
                        )
                    )
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
            }
        }
    }

    @Composable
    override fun topMenu(trayState: TrayState, needRestart: MutableState<Boolean>, needRefresh: MutableState<Boolean>) {
        val windowState =
            rememberWindowState(position = Aligned(alignment = Alignment.Center), size = DpSize(500.dp, 350.dp))
        var isOnTop by remember { internalIsOnTop }

        if (!isOnTop || roundDatas.isEmpty() || lastTime !in roundDatas) return

        val isInWorld by isInWorld
        val lastTime by mutableStateOf(lastTime)
        val roundData by mutableStateOf(roundDatas[lastTime]!!)
        val roundDetail by mutableStateOf(roundData.roundDetail)
        val players = remember { mutableStateListOf<PlayerData>() }

        players.clear()
        players.addAll(roundDetail.players)

        val terrorsList = remember { mutableStateListOf<Int>() }

        terrorsList.clear()
        terrorsList.addAll(roundDetail.terrors)

        val roundType by mutableStateOf(roundData.roundType)
        val terrors by mutableStateOf(Terrors(terrorsList, roundType))
        val scrollState = rememberScrollState()
        val youDeath by "gui.text.round_datas.you_death".i18nState()
        val youAlive by "gui.text.round_datas.you_alive".i18nState()
        val map by mutableStateOf(roundDetail.map)
        val isDeath by mutableStateOf(roundDetail.isDeath)
        val isWon by mutableStateOf(roundDetail.isWon)
        val terrorsNames = remember { mutableStateListOf<String>() }

        terrorsNames.clear()
        terrorsNames.addAll(terrors.names)

        val unknown by "gui.text.round_datas.unknown".i18nState()
        val playerWon by "gui.text.round_datas.player_won".i18nState()
        val playerLost by "gui.text.round_datas.player_lost".i18nState()
        val left by "gui.text.round_datas.left".i18nState()
        val roundIsStillInProgress by "gui.text.round_datas.round_is_still_in_progress".i18nState()
        val roundTypeText by "gui.text.round_datas.round_type".i18nState(RoundTypeConvert.getTextOfRound(roundType))
        val playersText by "gui.text.round_datas.players".i18nState(
            players.filter { it.status == PlayerStatus.Alive }.size,
            players.size
        )
        val terrorsText by "gui.text.round_datas.terrors".i18nState(terrorsNames.size)
        val notInTon by "gui.text.round_datas.not_in_ton".i18nState(terrorsNames.size)

        val textTime by mutableStateOf(roundDetail.time)
        val realTime by mutableStateOf(TimerManager.get(RoundTimerID))
        val time = if (textTime < 0) realTime else textTime
        val duration = time.toDuration(DurationUnit.MILLISECONDS)
        val hours = duration.inWholeHours
        val minutes = duration.minus(hours.toDuration(DurationUnit.HOURS)).inWholeMinutes
        val seconds = duration.minus(hours.toDuration(DurationUnit.HOURS))
            .minus(minutes.toDuration(DurationUnit.MINUTES)).inWholeSeconds
        val milliSeconds =
            duration.minus(hours.toDuration(DurationUnit.HOURS)).minus(minutes.toDuration(DurationUnit.MINUTES))
                .minus(seconds.toDuration(DurationUnit.SECONDS)).inWholeMilliseconds
        val hoursText = hours.toString().padStart(2, '0')
        val minutesText = minutes.toString().padStart(2, '0')
        val secondsText = seconds.toString().padStart(2, '0')
        val milliSecondsText = milliSeconds.toString().padStart(2, '0')

        val playerTime by mutableStateOf(roundDetail.playerTime)
        val playerDuration = playerTime.toDuration(DurationUnit.MILLISECONDS)
        val playerHours = playerDuration.inWholeHours
        val playerMinutes = playerDuration.minus(playerHours.toDuration(DurationUnit.HOURS)).inWholeMinutes
        val playerSeconds = playerDuration.minus(playerHours.toDuration(DurationUnit.HOURS))
            .minus(playerMinutes.toDuration(DurationUnit.MINUTES)).inWholeSeconds
        val playerMilliSeconds = playerDuration.minus(playerHours.toDuration(DurationUnit.HOURS))
            .minus(playerMinutes.toDuration(DurationUnit.MINUTES))
            .minus(playerSeconds.toDuration(DurationUnit.SECONDS)).inWholeMilliseconds
        val playerHoursText = playerHours.toString().padStart(2, '0')
        val playerMinutesText = playerMinutes.toString().padStart(2, '0')
        val playerSecondsText = playerSeconds.toString().padStart(2, '0')
        val playerMilliSecondsText = playerMilliSeconds.toString().padStart(2, '0')

        Window(
            onCloseRequest = { isOnTop = false },
            visible = true,
            title = "gui.title.window.round_viewer".i18n(),
            state = windowState,
            alwaysOnTop = true
        ) {
            CupcakeEXTheme {
                SelectionContainer {
                    Column(
                        Modifier.fillMaxWidth().padding(10.dp).verticalScroll(scrollState),
                        verticalArrangement = Arrangement.Top
                    ) {
                        if (!isInWorld || logWatcher == null) return@Column
                        if (!logWatcher!!.isInTON.value) {
                            Text(notInTon)
                            return@Column
                        }

                        Text(map)
                        Text(roundTypeText)

                        if (isDeath)
                            Text(youDeath)
                        else
                            Text(youAlive)

                        when (isWon) {
                            WonOrLost.UnKnown -> Text(unknown)
                            WonOrLost.Won -> Text(playerWon)
                            WonOrLost.Lost -> Text(playerLost)
                            WonOrLost.Left -> Text(left)
                            WonOrLost.InProgress -> Text(roundIsStillInProgress)
                        }

                        if (playerTime > 0)
                            Text("$playerHoursText:$playerMinutesText:$playerSecondsText.$playerMilliSecondsText / $hoursText:$minutesText:$secondsText.$milliSecondsText")
                        else
                            Text("$hoursText:$minutesText:$secondsText.$milliSecondsText")

                        if (terrorsNames.isNotEmpty())
                            Text(terrorsText)
                        Column(Modifier.padding(10.dp)) {
                            terrorsNames.forEach {
                                Box(
                                    Modifier.fillMaxWidth()
                                        .background(Color(0, 0, 0, 40))
                                        .padding(5.dp)
                                ) {
                                    Text(it)
                                }
                            }
                        }

                        if (players.isNotEmpty())
                            Text(playersText)
                        Column(Modifier.padding(10.dp)) {
                            players.forEach {
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
                    }
                }
            }
        }
    }
}