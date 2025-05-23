package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.rememberWindowState
import androidx.navigation.NavHostController
import io.github.t2penbix99wcoxkv3a4g.tonsign.EventHandle
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.RoundData
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.getAll
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.swapList
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.timeOf
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.SaveManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.TimerManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.l10n
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.l10nState
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundTypeConvert
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.Terrors
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.WonOrLost
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.CupcakeEXTheme
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.tray.TrayItem
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.tray.TrayRoundDatas
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

    private val roundData: MutableState<RoundData?> = mutableStateOf(null)
    private val roundDatas = mutableStateListOf<RoundData>()
    private val queries = SaveManager.database.saveQueries

    @Composable
    override fun icon() {
        Icon(Icons.Default.History, contentDescription = title.l10n())
    }

    init {
        refresh()
        EventBus.register(this)
    }

    // TODO: completely use sql data
    private fun refresh() {
        roundDatas.swapList(queries.getAll())
    }

    @Subscribe(Events.OnRoundOver)
    private fun onRoundOver(event: OnRoundOverEvent) = refresh()

    @Subscribe(Events.OnRoundStart)
    private fun onRoundStart(event: OnRoundStartEvent) = refresh()

    @Subscribe(Events.OnRoundLost)
    private fun onRoundLost(event: OnRoundLostEvent) = refresh()

    @Subscribe(Events.OnRoundDeath)
    private fun onRoundDeath(event: OnRoundDeathEvent) = refresh()

    @Subscribe(Events.OnRoundWon)
    private fun onRoundWon(event: OnRoundWonEvent) = refresh()

    @Subscribe(Events.OnKillerSet)
    private fun onKillerSet(event: OnKillerSetEvent) = refresh()

    @Subscribe(Events.OnHideTerrorShowUp)
    private fun onHideTerrorShowUp(event: OnHideTerrorShowUpEvent) = refresh()

    @Subscribe(Events.OnPlayerLeftRoom)
    private fun onPlayerLeftRoom(event: OnPlayerLeftRoomEvent) = refresh()

    @Subscribe(Events.OnLeftTON)
    private fun onLeftTON(event: OnLeftTONEvent) = refresh()

    @Subscribe(Events.OnJoinTON)
    private fun onJoinTON(event: OnJoinTONEvent) = refresh()

    @Composable
    override fun view(navController: NavHostController, padding: PaddingValues) {
        val roundData: MutableState<RoundData?> = remember { roundData }
        var isOnTop by remember { internalIsOnTop }

        SideEffect {
            refresh()
        }

        val searchButtons = listOf(
            SearchButton({
                refresh()
            }, Icons.Default.Refresh, "refresh"),
            SearchButton({
                isOnTop = true
            }, Icons.AutoMirrored.Filled.OpenInNew, "IsOnTop")
        )

        tableView(
            roundData, roundDatas, searchButtons,
            filter = listOf(
                TimeValue("time"),
                RoundTypeValue("roundType")
            ),
            onRowSelection = {
                roundData.value = it
            }
        )
    }

    private fun playerStatus(status: PlayerStatus): String {
        val unknown by "gui.text.round_datas.unknown".l10nState()
        val alive by "gui.text.round_datas.alive".l10nState()
        val death by "gui.text.round_datas.death".l10nState()
        val left by "gui.text.round_datas.left".l10nState()

        return when (status) {
            PlayerStatus.Unknown -> unknown
            PlayerStatus.Alive -> alive
            PlayerStatus.Death -> death
            PlayerStatus.Left -> left
        }
    }

    @Composable
    override fun detailView(navController: NavHostController, padding: PaddingValues) {
        val roundDataTry: MutableState<RoundData?> = remember { roundData }
        if (roundDataTry.value == null)
            return

        val roundData = roundDataTry.value!!
        val terrors = Terrors(roundData.terrors, roundData.roundType, roundData.roundFlags)
        val scrollState = rememberScrollState()
        val youDeath by "gui.text.round_datas.you_death".l10nState()
        val youAlive by "gui.text.round_datas.you_alive".l10nState()

        val textTime by mutableStateOf(roundData.roundTime)
        val realTime by mutableStateOf(TimerManager.get(EventHandle.ROUND_TIMER_ID))
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

        val playerTime by mutableStateOf(roundData.playerTime)
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
                Text(roundData.map)
                Text(RoundTypeConvert.getTextOfRound(roundData.roundType))

                if (roundData.isDeath)
                    Text(youDeath)
                else
                    Text(youAlive)

                when (roundData.isWon) {
                    WonOrLost.UnKnown -> Text("gui.text.round_datas.unknown".l10n())
                    WonOrLost.Won -> Text("gui.text.round_datas.player_won".l10n())
                    WonOrLost.Lost -> Text("gui.text.round_datas.player_lost".l10n())
                    WonOrLost.Left -> Text("gui.text.round_datas.left".l10n())
                    WonOrLost.InProgress -> Text("gui.text.round_datas.round_is_still_in_progress".l10n())
                }

                if (playerTime > 0)
                    Text("$playerHoursText:$playerMinutesText:$playerSecondsText.$playerMilliSecondsText / $hoursText:$minutesText:$secondsText.$milliSecondsText")
                else
                    Text("$hoursText:$minutesText:$secondsText.$milliSecondsText")

                if (terrors.names.isNotEmpty())
                    Text("gui.text.round_datas.terrors".l10n(terrors.names.size))
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

                if (roundData.players.isNotEmpty())
                    Text(
                        "gui.text.round_datas.players".l10n(
                            roundData.players.filter { it.status == PlayerStatus.Alive }.size,
                            roundData.players.size
                        )
                    )
                Column(Modifier.padding(10.dp)) {
                    roundData.players.forEach {
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
    override fun topMenu() {
        val windowState =
            rememberWindowState(position = Aligned(alignment = Alignment.Center), size = DpSize(500.dp, 350.dp))
        var isOnTop by remember { internalIsOnTop }

        if (!isOnTop) return

        Window(
            onCloseRequest = { isOnTop = false },
            visible = true,
            title = "gui.title.window.round_viewer".l10n(),
            state = windowState,
            alwaysOnTop = true
        ) {
            CupcakeEXTheme {
                val isInWorld by remember { EventHandle.isInWorld }
                val lastTime by EventHandle.currentTime
                val roundDataState = mutableStateOf(queries.timeOf(lastTime))
                if (roundDatas.isEmpty() || roundDataState.value == null) return@CupcakeEXTheme
                val roundData = roundDataState.value!!
                val players = remember { mutableStateListOf<PlayerData>() }

                players.swapList(roundData.players)

                val terrorsList = remember { mutableStateListOf<Int>() }

                terrorsList.swapList(roundData.terrors)

                val roundType by mutableStateOf(roundData.roundType)
                val roundFlags by mutableStateOf(roundData.roundFlags)
                val terrors by mutableStateOf(Terrors(terrorsList, roundType, roundFlags))
                val scrollState = rememberScrollState()
                val youDeath by "gui.text.round_datas.you_death".l10nState()
                val youAlive by "gui.text.round_datas.you_alive".l10nState()
                val map by mutableStateOf(roundData.map)
                val isDeath by mutableStateOf(roundData.isDeath)
                val isWon by mutableStateOf(roundData.isWon)
                val terrorsNames = remember { mutableStateListOf<String>() }

                terrorsNames.swapList(terrors.names)

                val unknown by "gui.text.round_datas.unknown".l10nState()
                val playerWon by "gui.text.round_datas.player_won".l10nState()
                val playerLost by "gui.text.round_datas.player_lost".l10nState()
                val left by "gui.text.round_datas.left".l10nState()
                val roundIsStillInProgress by "gui.text.round_datas.round_is_still_in_progress".l10nState()
                val playersText by "gui.text.round_datas.players".l10nState(
                    players.filter { it.status == PlayerStatus.Alive }.size,
                    players.size
                )
                val terrorsText by "gui.text.round_datas.terrors".l10nState(terrorsNames.size)
                val notInTon by "gui.text.round_datas.not_in_ton".l10nState(terrorsNames.size)

                val textTime by mutableStateOf(roundData.roundTime)
                val realTime by mutableStateOf(TimerManager.get(EventHandle.ROUND_TIMER_ID))
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

                val playerTime by mutableStateOf(roundData.playerTime)
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
                        Modifier.fillMaxWidth().padding(10.dp).verticalScroll(scrollState),
                        verticalArrangement = Arrangement.Top
                    ) {
                        if (!isInWorld || logWatcher == null) return@Column
                        if (!logWatcher!!.isInTON.value) {
                            Text(notInTon)
                            return@Column
                        }

                        val recentRounds by logWatcher!!.getRecentRoundsLogState

                        Text(map)
                        Text(RoundTypeConvert.getTextOfRound(roundType))

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

                        if (recentRounds.isNotBlank())
                            Text("gui.text.main.recent_rounds".l10n(recentRounds))

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