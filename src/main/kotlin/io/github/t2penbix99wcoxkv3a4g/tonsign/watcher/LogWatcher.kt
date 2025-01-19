package io.github.t2penbix99wcoxkv3a4g.tonsign.watcher

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.github.t2penbix99wcoxkv3a4g.tonsign.OSCSender
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.firstPath
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.lastPath
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.middlePath
import io.github.t2penbix99wcoxkv3a4g.tonsign.exception.UnknownRoundTypeException
import io.github.t2penbix99wcoxkv3a4g.tonsign.exception.WrongRecentRoundException
import io.github.t2penbix99wcoxkv3a4g.tonsign.interpreter.LogInterpreter
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.debug
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.error
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.Terror
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class LogWatcher(logFile: File) {
    companion object {
        val Default: LogWatcher
            get() = LogWatcher(findLatestLog())

        fun findLatestLog(): File {
            val files = Utils.logDirectory.toFile().listFiles { _, filename ->
                filename.endsWith(".txt")
            }

            requireNotNull(files)

            if (files.isEmpty())
                throw FileNotFoundException("exception.no_log_file".i18n())

            files.sortWith { f1, f2 ->
                val compare = f1.lastModified() > f2.lastModified()
                if (compare)
                    return@sortWith -1
                else
                    return@sortWith 1
            }

            Logger.info({ "log.current_log_running" }, files.first().name)
            return files.first()
        }

        private const val MASTER_CLIENT_SWITCHED_KEYWORD = "OnMasterClientSwitched"
        private const val SAVING_AVATAR_DATA_KEYWORD = "Saving Avatar Data:"
        private const val FOUND_SDK3_AVATAR_DESCRIPTOR_KEYWORD = "Found SDK3 avatar descriptor"
        private const val ROUND_TYPE_IS_KEYWORD = "round type is"
        private const val ROUND_OVER_KEYWORD = "RoundOver"
        private const val WORLD_JOIN_KEYWORD =
            "Memory Usage: after world loaded "
        private const val WORLD_TON_KEYWORD = "wrld_a61cdabe-1218-4287-9ffc-2a4d1414e5bd"
        private const val WORLD_LEFT_KEYWORD = "OnLeftRoom"
        private const val WORLD_PLAYER_LEFT_KEYWORD = "OnPlayerLeft "
        private const val WORLD_PLAYER_JOINED_KEYWORD = "OnPlayerJoined "
        private const val ROUND_WON_KEYWORD = "Player Won"
        private const val ROUND_LOST_KEYWORD = "Player lost,"
        private const val ROUND_DEATH_KEYWORD = "You died."
        private const val PLAYER_DEATH_KEYWORD = "DEATH"
        private const val KILLER_MATRIX_KEYWORD = "Killers have been set - "
        private const val KILLER_MATRIX_UNKNOWN = "Killers is unknown - "
        private const val KILLER_MATRIX_REVEAL = "Killers have been revealed - "
        private const val KILLER_ROUND_TYPE_KEYWORD = " // Round type is "
        private const val TERROR_HUNGRY_HOME_INVADER_KEYWORD = "HungryHomeInvader"
        private const val TERROR_ATRACHED_KEYWORD = "Atrached"
        private const val TERROR_WILD_YET_BLOODTHIRSTY_CREATURE_KEYWORD = "Wild Yet Bloodthirsty Creature"
        private const val TERROR_TRANSPORTATION_TRIO_THE_DRIFTER_KEYWORD = "Transportation Trio & The Drifter"
        private const val TERROR_RED_MIST_APPARITION_KEYWORD = "Red Mist Apparition"
        private const val TERROR_BALDI_KEYWORD = "Baldi"
        private const val TERROR_SHADOW_FREDDY_KEYWORD = "Shadow Freddy"
        private const val TERROR_SEARCHLIGHTS_KEYWORD = "Searchlights"
        private const val TERROR_ALTERNATES_KEYWORD = "Alternates"
        private const val AND_THE_ROUND_TYPE_IS_KEYWORD = " and the round type is "
        private const val ROUND_MAP_LOCATION_KEYWORD = "This round is taking place at "
        private const val BEHAVIOUR_KEYWORD = "Behaviour"
        private const val HANDLE_APPLICATION_QUIT_KEYWORD = "HandleApplicationQuit"
        private const val RANDOM_COUNT_RESET = 3
        private const val ATTEMPTING_TO_LOAD_STRING_FROM_URL_KEYWORD = "Attempting to load String from URL"
        private const val STRING_DOWNLOAD_KEYWORD = "String Download"
        private const val WINTER_KEYWORD = "winter!"
    }

    private val roundLog = mutableListOf<GuessRoundType>()
    private val roundFlags = EnumSet.of(RoundFlag.WaitingFirstSpecial)
    private val logInterpreter = LogInterpreter(logFile)
    private var lastPredictionForOSC = false
    private var lastPredictionTabunForOSC = false
    private var lastPrediction = GuessRoundType.NIL
    private var lastRoundType = RoundType.Unknown
    private var randomRound = RandomRoundType.Unknown
    private var randomCount = 0
    private var wrongCount = 0
    private var isTONLoaded = false
    private var hasFirstSend = false
    val isInTON = mutableStateOf(false)

    init {
        EventBus.register(this)
    }

    private fun isAlternatePattern(): Boolean {
        if (roundLog.size < 3) {
            return if (roundLog.size > 1)
                roundLog[1] == GuessRoundType.Special
            else
                false
        }

        if (roundLog.size < 4) {
            val last = roundLog.takeLast(3)
            return last[0] != last[1] && last[1] != last[2]
        }

        val last = roundLog.takeLast(4)
        return last[0] == last[2] && last[1] == last[3]
    }

    private fun predictNextRound(round: RoundType): GuessRoundType {
        if (roundLog.size < 2)
            return GuessRoundType.Classic

        val last = roundLog.takeLast(2)

        if (last[0] == GuessRoundType.Special && last[1] == GuessRoundType.Special) {
            Logger.info { "log.host_left_before" }
            roundLog.removeLast()
        }

        val isAlternate = isAlternatePattern()

        randomCount++

        when {
            randomRound == RandomRoundType.Unknown -> {
                randomRound = if (isAlternate) RandomRoundType.FAST else RandomRoundType.NORMAL
                randomCount = RANDOM_COUNT_RESET
            }

            randomRound == RandomRoundType.NORMAL && isAlternate -> {
                randomRound = RandomRoundType.FAST
                randomCount = RANDOM_COUNT_RESET
                Logger.debug<LogWatcher> { "Random type seem is wrong, change to $randomRound" }
            }

            randomRound == RandomRoundType.FAST && randomCount > 2 -> {
                val last2 = roundLog.takeLast(3)
                if (last2.size > 2 && last2[0] == last2[1] || last2[1] == last2[2]) {
                    randomRound = RandomRoundType.NORMAL
                    randomCount = RANDOM_COUNT_RESET
                    Logger.debug<LogWatcher> { "Random type seem is wrong, change to $randomRound" }
                }
            }
        }

        Logger.debug<LogWatcher> { "randomCount: $randomCount, randomRound: $randomRound" }

        when (randomRound) {
            RandomRoundType.NORMAL -> {
                when {
                    last[0] == GuessRoundType.Special && last[1] == GuessRoundType.Classic -> roundFlags.addSafe(
                        RoundFlag.NotSure
                    )

                    last[0] == GuessRoundType.Special && lastPrediction == GuessRoundType.Classic && !RoundTypeConvert.isCorrectGuess(
                        lastPrediction,
                        round
                    ) -> roundFlags.removeSafe(RoundFlag.NotSure)

                    last[0] == GuessRoundType.Classic && lastPrediction == GuessRoundType.Special && !RoundTypeConvert.isCorrectGuess(
                        lastPrediction,
                        round
                    ) -> roundFlags.removeSafe(RoundFlag.NotSure)

                    last[1] == GuessRoundType.Special && RoundTypeConvert.isCorrectGuess(
                        lastPrediction,
                        round
                    ) -> roundFlags.removeSafe(RoundFlag.NotSure)
                }
            }

            RandomRoundType.FAST -> {
                when {
                    last[0] == GuessRoundType.Special && last[1] == GuessRoundType.Classic -> roundFlags.addSafe(
                        RoundFlag.NotSure
                    )

                    last[0] == GuessRoundType.Classic && last[1] == GuessRoundType.Special -> roundFlags.removeSafe(
                        RoundFlag.NotSure
                    )
                }
            }

            else -> {}
        }

        Logger.debug<LogWatcher> {
            "last[0]: ${last[0]}, last[1]: ${last[1]} lastPrediction: $lastPrediction, isCorrectGuess: ${
                RoundTypeConvert.isCorrectGuess(
                    lastPrediction,
                    round
                )
            }"
        }

        if (randomRound == RandomRoundType.FAST)
            return if (roundLog.last() == GuessRoundType.Special) GuessRoundType.Classic else GuessRoundType.Special
        else {
            val last2 = roundLog.takeLast(2)
            return if (last2[0] == GuessRoundType.Classic && last2[1] == GuessRoundType.Classic) GuessRoundType.Special else GuessRoundType.Classic
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getRecentRoundsLog(maxRecent: Int): String {
        return roundLog.takeLast(maxRecent).joinToString {
            when (it) {
                GuessRoundType.Classic -> "log.recent_rounds_log_classic".i18n()
                GuessRoundType.Special -> "log.recent_rounds_log_special".i18n()

                else -> throw WrongRecentRoundException("exception.wrong_recent_round".i18n(it))
            }
        }
    }

    private val _getRecentRoundsLogState = mutableStateOf("")

    val getRecentRoundsLogState: MutableState<String>
        get() {
            _getRecentRoundsLogState.value = getRecentRoundsLog(ConfigManager.config.maxRecentRounds)
            return _getRecentRoundsLogState
        }

    @Suppress("MemberVisibilityCanBePrivate")
    fun clear() {
        roundLog.clear()
        lastPredictionForOSC = false
        lastPredictionTabunForOSC = false
        lastPrediction = GuessRoundType.NIL
        randomRound = RandomRoundType.Unknown
        randomCount = 0
        roundFlags.removeAll { it != RoundFlag.Winter }
        roundFlags.add(RoundFlag.WaitingFirstSpecial)
    }

    private fun updateRoundLog(round: RoundType) {
        var classification = round.classifyRound()

        when (classification) {
            GuessRoundType.Exempt -> {
                when {
                    (round == RoundType.MysticMoon && roundFlags.contains(RoundFlag.MysticMoonFinish)) || (round == RoundType.BloodMoon && roundFlags.contains(
                        RoundFlag.BloodMoonFinish
                    )) || (round == RoundType.Twilight && roundFlags.contains(RoundFlag.TwilightFinish)) || (round == RoundType.Solstice && roundFlags.contains(
                        RoundFlag.SolsticeFinish
                    )) -> {
                        roundFlags.removeSafe(RoundFlag.NotSure)
                        classification = GuessRoundType.Classic
                    }

                    else -> {
                        when (round) {
                            RoundType.MysticMoon -> roundFlags.addSafe(RoundFlag.MysticMoonFinish)
                            RoundType.BloodMoon -> roundFlags.addSafe(RoundFlag.BloodMoonFinish)
                            RoundType.Twilight -> roundFlags.addSafe(RoundFlag.TwilightFinish)
                            RoundType.Solstice -> roundFlags.addSafe(RoundFlag.SolsticeFinish)
                            else -> {}
                        }

                        if (roundLog.size >= 2) {
                            val last = roundLog.takeLast(2)
                            when {
                                last[0] == GuessRoundType.Classic && last[1] == GuessRoundType.Classic -> classification =
                                    GuessRoundType.Special

                                last[0] == GuessRoundType.Classic && last[1] == GuessRoundType.Special -> classification =
                                    GuessRoundType.Classic

                                last[0] == GuessRoundType.Special && last[1] == GuessRoundType.Classic -> classification =
                                    if (isAlternatePattern()) GuessRoundType.Special else GuessRoundType.Classic
                            }
                        } else
                            classification = GuessRoundType.Classic
                        roundFlags.addSafe(RoundFlag.NotSure)
                    }
                }
            }

            GuessRoundType.Special -> roundFlags.removeSafe(RoundFlag.WaitingFirstSpecial)

            else -> {}
        }

        roundLog.add(classification)
    }

    @Subscribe(Events.OnReadLog)
    private fun onReadLog(event: OnReadLogEvent) {
        val log = event.logEvent

        when {
            WINTER_KEYWORD in log.msg -> {
                Logger.info { "log.is_winter" }
                if (!isTONLoaded) return
                roundFlags.add(RoundFlag.Winter)
            }

            HANDLE_APPLICATION_QUIT_KEYWORD in log.msg -> {
                Logger.info { "log.vrchat_quit" }
                EventBus.publish(OnVRChatQuitEvent())
            }

            MASTER_CLIENT_SWITCHED_KEYWORD in log.msg -> {
                if (!isTONLoaded) return
                Logger.info { "log.host_just_left" }
                clear()
                OSCSender.send(true)
                OSCSender.sendTabun(false)
                lastPredictionForOSC = true
                lastPredictionTabunForOSC = false
                EventBus.publish(OnTONMasterClientSwitchedEvent())
            }

            SAVING_AVATAR_DATA_KEYWORD in log.msg -> {
                Logger.info { "log.saving_avatar_data" }
                EventBus.publish(OnSavingAvatarDataEvent())
            }

            FOUND_SDK3_AVATAR_DESCRIPTOR_KEYWORD in log.msg -> {
                Logger.info { "log.found_sdk3_avatar_descriptor" }

                if (isTONLoaded) {
                    OSCSender.send(lastPredictionForOSC)
                    OSCSender.sendTabun(lastPredictionTabunForOSC)

                    if (ConfigManager.config.automaticTurnOnSign)
                        OSCSender.sendOn(true)
                    if (roundFlags.contains(RoundFlag.WaitingFirstSpecial))
                        OSCSender.sendTabun(true)
                }

                EventBus.publish(OnFoundSDK3AvatarDescriptorEvent())
            }

            log.name == STRING_DOWNLOAD_KEYWORD && ATTEMPTING_TO_LOAD_STRING_FROM_URL_KEYWORD in log.msg -> {
                val urlStr = log.msg.lastPath(ATTEMPTING_TO_LOAD_STRING_FROM_URL_KEYWORD).trim()
                val url = urlStr.substring(1, urlStr.length - 1)
                Logger.debug<LogWatcher> { "url: $url, msg: ${log.msg}" }
                Logger.info({ "log.load_string" }, url)

                if (isTONLoaded && !hasFirstSend) {
                    hasFirstSend = true
                    if (ConfigManager.config.automaticTurnOnSign)
                        OSCSender.sendOn(true)
                    OSCSender.sendTabun(true)
                }

                EventBus.publish(OnStringDownloadEvent(url))
            }

            WORLD_JOIN_KEYWORD in log.msg -> {
                val worldId = log.msg.lastPath(WORLD_JOIN_KEYWORD).middlePath('[', ']')

                Logger.info({ "log.joined_instance" }, worldId)
                EventBus.publish(OnJoinRoomEvent(worldId))

                if (worldId == WORLD_TON_KEYWORD) {
                    isTONLoaded = true
                    isInTON.value = true
                    Logger.info { "log.is_join_ton" }
                    EventBus.publish(OnJoinTONEvent())
                }
            }

            WORLD_LEFT_KEYWORD in log.msg -> {
                Logger.info { "log.left_instance" }
                EventBus.publish(OnLeftRoomEvent())

                if (isTONLoaded) {
                    isTONLoaded = false
                    hasFirstSend = false
                    isInTON.value = false
                    Logger.info { "log.is_left_ton" }
                    if (ConfigManager.config.automaticTurnOnSign)
                        OSCSender.sendOn(false)
                    EventBus.publish(OnLeftTONEvent())
                    clear()
                }
            }

            WORLD_PLAYER_LEFT_KEYWORD in log.msg -> {
                val player = log.msg.lastPath(WORLD_PLAYER_LEFT_KEYWORD).trim()
                val name = player.firstPath('(').trim()
                val id = player.middlePath('(', ')').trim()

                Logger.info({ "log.user_left_instance" }, name, id)
                EventBus.publish(OnPlayerLeftRoomEvent(PlayerData(name, id, PlayerStatus.Left, null)))
            }

            log.name == BEHAVIOUR_KEYWORD && WORLD_PLAYER_JOINED_KEYWORD in log.msg -> {
                val player = log.msg.lastPath(WORLD_PLAYER_JOINED_KEYWORD).trim()
                val name = player.firstPath('(').trim()
                val id = player.middlePath('(', ')').trim()

                Logger.info({ "log.user_joined_instance" }, name, id)
                EventBus.publish(OnPlayerJoinedRoomEvent(PlayerData(name, id, PlayerStatus.Alive, null)))
            }

            ROUND_OVER_KEYWORD in log.msg -> {
                if (!isTONLoaded) return
                Logger.info { "log.round_over" }
                EventBus.publish(OnRoundOverEvent(lastPrediction, roundFlags))
            }

            ROUND_WON_KEYWORD in log.msg -> {
                if (!isTONLoaded) return
                Logger.info { "log.round_won" }
                EventBus.publish(OnRoundWonEvent())
            }

            ROUND_LOST_KEYWORD in log.msg -> {
                if (!isTONLoaded) return
                Logger.info { "log.round_lost" }
                EventBus.publish(OnRoundLostEvent())
            }

            ROUND_DEATH_KEYWORD in log.msg -> {
                if (!isTONLoaded) return
                Logger.info { "log.round_death" }
                EventBus.publish(OnRoundDeathEvent())
            }

            log.name == PLAYER_DEATH_KEYWORD -> {
                if (!isTONLoaded) return
                val name = log.msg.middlePath('[', ']').trim()
                val msg = log.msg.lastPath(']').trim()

                Logger.info({ "log.player_death" }, name, msg)
                EventBus.publish(OnPlayerDeathEvent(PlayerData(name, null, PlayerStatus.Death, msg)))
            }

            TERROR_HUNGRY_HOME_INVADER_KEYWORD in log.msg -> {
                if (!isTONLoaded || lastRoundType != RoundType.Classic) return
                // TODO: Log
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE))
            }

            TERROR_ATRACHED_KEYWORD in log.msg -> {
                if (!isTONLoaded || lastRoundType != RoundType.Classic) return
                // TODO: Log
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE + 1))
            }

            TERROR_WILD_YET_BLOODTHIRSTY_CREATURE_KEYWORD in log.msg -> {
                if (!isTONLoaded || lastRoundType != RoundType.Classic) return
                // TODO: Log
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE + 2))
            }

            TERROR_TRANSPORTATION_TRIO_THE_DRIFTER_KEYWORD in log.msg -> {
                if (!isTONLoaded || lastRoundType != RoundType.Unbound) return
                // TODO: Log
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE))
            }

            TERROR_RED_MIST_APPARITION_KEYWORD in log.msg -> {
                if (!isTONLoaded || lastRoundType != RoundType.EightPages) return
                // TODO: Log
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE))
            }

            TERROR_BALDI_KEYWORD in log.msg -> {
                if (!isTONLoaded || lastRoundType != RoundType.EightPages) return
                // TODO: Log
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE + 1))
            }

            TERROR_SHADOW_FREDDY_KEYWORD in log.msg -> {
                if (!isTONLoaded || lastRoundType != RoundType.EightPages) return
                // TODO: Log
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE + 2))
            }

            TERROR_SEARCHLIGHTS_KEYWORD in log.msg -> {
                if (!isTONLoaded || lastRoundType != RoundType.EightPages) return
                // TODO: Log
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE + 3))
            }

            TERROR_ALTERNATES_KEYWORD in log.msg -> {
                if (!isTONLoaded || lastRoundType != RoundType.EightPages) return
                // TODO: Log
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE + 4))
            }

            // https://github.com/ChrisFeline/ToNSaveManager/blob/main/Windows/MainWindow.cs
            KILLER_MATRIX_KEYWORD in log.msg || KILLER_MATRIX_UNKNOWN in log.msg || KILLER_MATRIX_REVEAL in log.msg -> {
                if (!isTONLoaded) return
                val isUnknown = KILLER_MATRIX_UNKNOWN in log.msg
                val isRevealed = KILLER_MATRIX_REVEAL in log.msg
                val killerMatrix = arrayListOf(Terror.UNKNOWN, Terror.UNKNOWN, Terror.UNKNOWN)

                if (!isUnknown) {
                    val kMatrixRaw = log.msg.middlePath(
                        if (isRevealed) KILLER_MATRIX_REVEAL else KILLER_MATRIX_KEYWORD,
                        KILLER_ROUND_TYPE_KEYWORD
                    ).trim().split(' ')
                    for (i in 0..<killerMatrix.size) {
                        killerMatrix[i] = runCatching { kMatrixRaw[i].trim().toInt() }.getOrElse { -1 }
                    }
                }

                Logger.info({ "log.killer_id_set" }, killerMatrix[0], killerMatrix[1], killerMatrix[2])
                EventBus.publish(OnKillerSetEvent(killerMatrix))
            }

            ROUND_TYPE_IS_KEYWORD in log.msg -> {
                if (!isTONLoaded) return
                var possibleRoundType = log.msg.lastPath(ROUND_TYPE_IS_KEYWORD).trim()
                val possibleRoundTypeForPrint = possibleRoundType

                Logger.debug<LogWatcher> { "possibleRoundType: '${possibleRoundType}'" }

                possibleRoundType = possibleRoundType.jpToEn()

                if (possibleRoundType !in RoundTypeConvert.ENRoundTypes)
                    return

                val roundType = possibleRoundType.getTypeOfRound()

                if (!RoundTypeConvert.isCorrectGuess(lastPrediction, roundType)) {
                    wrongCount++

                    val isSpecialOrNot = roundType.isSpecialOrClassic()

                    Logger.debug<LogWatcher> { "Guess is wrong, Last Guess: $lastPrediction, Actually is: $isSpecialOrNot, Wrong Count: $wrongCount" }
                }

                val mapString = log.msg.middlePath(ROUND_MAP_LOCATION_KEYWORD, AND_THE_ROUND_TYPE_IS_KEYWORD).trim()
                val idAndMap = mapString.split('(')
                val map = idAndMap[0].trim()
                val id = idAndMap[1].filter { it.isDigit() }.trim()

                EventBus.publish(OnRoundStartEvent(log.time, roundType, map, id.toInt()))

                updateRoundLog(roundType)
                Logger.info(
                    { "log.new_round_started" },
                    runCatching { RoundTypeConvert.getTextOfRound(roundType) }.getOrElse {
                        when (it) {
                            is UnknownRoundTypeException -> {
                                Logger.error<LogWatcher>(
                                    it,
                                    { "exception.unknown_round_type" },
                                    possibleRoundTypeForPrint
                                )
                                "exception.unknown_round_type_simple".i18n(possibleRoundTypeForPrint)
                            }

                            else -> throw it
                        }
                    }
                )

                val classic = "log.predict_next_round_classic".i18n()
                val special = "log.predict_next_round_special".i18n()
                val prediction = predictNextRound(roundType)
                val recentRoundsLog = getRecentRoundsLog(ConfigManager.config.maxRecentRounds)

                lastPrediction = prediction
                EventBus.publish(OnNextPredictionEvent(prediction, roundFlags))

                Logger.info(
                    { "log.next_round_should_be" },
                    recentRoundsLog,
                    if (prediction == GuessRoundType.Special) special else classic
                )
                Logger.debug<LogWatcher> { "Full Recent Rounds: ${getRecentRoundsLog(200)}" }

                val sendValue = prediction == GuessRoundType.Special
                val sendTabunValue =
                    roundFlags.contains(RoundFlag.WaitingFirstSpecial) || roundFlags.contains(RoundFlag.NotSure)

                // Send OSC message
                OSCSender.send(sendValue)
                OSCSender.sendTabun(sendTabunValue)
                lastPredictionForOSC = sendValue
                lastPredictionTabunForOSC = sendTabunValue
                lastRoundType = roundType
            }
        }
    }

    @Suppress("unused")
    fun read() = logInterpreter.read()

    suspend fun monitorRoundType() {
        while (true) {
            if (!VRChatWatcher.isVRChatRunning()) {
                Logger.info { "log.vrchat_not_found" }
                break
            }

            logInterpreter.read()
            delay(2000)
        }
    }
}