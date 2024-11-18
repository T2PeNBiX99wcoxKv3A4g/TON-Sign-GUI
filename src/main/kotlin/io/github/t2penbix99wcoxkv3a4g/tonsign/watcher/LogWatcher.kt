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
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.Terror
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileNotFoundException

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

            Logger.info("log.current_log_running", files.first().name)
            return files.first()
        }

        private const val BONUS_ACTIVE_KEYWORD = "BONUS ACTIVE!"
        private const val MASTER_CLIENT_SWITCHED_KEYWORD = "OnMasterClientSwitched"
        private const val SAVING_AVATAR_DATA_KEYWORD = "Saving Avatar Data:"
        private const val ROUND_TYPE_IS_KEYWORD = "round type is"
        private const val ROUND_OVER_KEYWORD = "RoundOver"
        private const val ROUND_FAST_CHANGE = 6
        private const val ROUND_NORMAL_CHANGE = 15
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
        private const val RANDOM_COUNT_CHANGE = 1
        private const val RANDOM_COUNT_RESET = 3
    }
    
    private val roundLog = mutableListOf<GuessRoundType>()
    private var lastPredictionForOSC = false
    private var lastPrediction = GuessRoundType.NIL
    private var lastRoundType = RoundType.Unknown
    private var bonusFlag = false
    private var randomRound = RandomRoundType.Unknown
    private var randomCount = 0
    private var lastRandomCount = -1
    private var wrongCount = 0
    private var isTONLoaded = false

    private val logInterpreter = LogInterpreter(logFile)
    val isInTON = mutableStateOf(false)

    init {
        EventBus.register(this)
    }

    private val maxRoundChange: Int
        get() {
            return when (randomRound) {
                RandomRoundType.FAST -> ROUND_FAST_CHANGE
                RandomRoundType.NORMAL -> ROUND_NORMAL_CHANGE
                else -> ROUND_NORMAL_CHANGE
            }
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

    private fun predictNextRound(): GuessRoundType {
        if (roundLog.size < 2)
            return GuessRoundType.Classic

        val last = roundLog.takeLast(2)

        if (last[0] == GuessRoundType.Special && last[1] == GuessRoundType.Special) {
            Logger.info("log.host_left_before")
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
                Logger.debug({ this::class.simpleName!! }, "Random type seem is wrong, change to $randomRound")
            }

            randomRound == RandomRoundType.FAST && randomCount > 2 -> {
                val last2 = roundLog.takeLast(3)
                if (last2.size > 2 && last2[0] == last2[1] || last2[1] == last2[2]) {
                    randomRound = RandomRoundType.NORMAL
                    randomCount = RANDOM_COUNT_RESET
                    Logger.debug({ this::class.simpleName!! }, "Random type seem is wrong, change to $randomRound")
                }
            }
        }

        if (randomCount >= maxRoundChange) {
            lastRandomCount = randomCount
            randomCount = RANDOM_COUNT_CHANGE
            randomRound = if (randomRound == RandomRoundType.FAST) RandomRoundType.NORMAL else RandomRoundType.FAST
            Logger.debug({ this::class.simpleName!! }, "Random start to change, now random round is $randomRound")
        }

        Logger.debug(
            { this::class.simpleName!! },
            "randomCount: $randomCount, randomRound: $randomRound, maxRoundChange: $maxRoundChange"
        )

        if (randomRound == RandomRoundType.FAST || bonusFlag)
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
        lastPrediction = GuessRoundType.NIL
        bonusFlag = false
        randomRound = RandomRoundType.Unknown
        randomCount = 0
    }

    private fun updateRoundLog(round: RoundType) {
        var classification = round.classifyRound()

        if (classification == GuessRoundType.Exempt) {
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
        }

        roundLog.add(classification)
    }

    @Subscribe("OnReadLog")
    private fun onReadLog(event: OnReadLogEvent) {
        val log = event.logEvent
        
        when {
            HANDLE_APPLICATION_QUIT_KEYWORD in log.msg -> {
                EventBus.publish(OnVRChatQuitEvent())
            }
            
            // TERROR NIGHTS STRING, Maybe useless right now
            BONUS_ACTIVE_KEYWORD in log.msg -> {
                bonusFlag = true
                Logger.info("log.think_terror_nights")
            }

            MASTER_CLIENT_SWITCHED_KEYWORD in log.msg -> {
                Logger.info("log.host_just_left")
                clear()
                OSCSender.send(true)
                lastPredictionForOSC = true
                EventBus.publish(OnTONMasterClientSwitchedEvent())
            }

            SAVING_AVATAR_DATA_KEYWORD in log.msg -> {
                Logger.info("log.saving_avatar_data")
                OSCSender.send(lastPredictionForOSC)
                EventBus.publish(OnSavingAvatarDataEvent())
            }

            WORLD_JOIN_KEYWORD in log.msg -> {
                val worldId = log.msg.lastPath(WORLD_JOIN_KEYWORD).middlePath('[', ']')
                
                EventBus.publish(OnJoinRoomEvent(worldId))

                if (worldId == WORLD_TON_KEYWORD) {
                    isTONLoaded = true
                    isInTON.value = true
                    Logger.info("log.is_join_ton")
                    EventBus.publish(OnJoinTONEvent())
                }
            }

            WORLD_LEFT_KEYWORD in log.msg -> {
                EventBus.publish(OnLeftRoomEvent())
                if (isTONLoaded) {
                    isInTON.value = false
                    Logger.info("log.is_left_ton")
                    EventBus.publish(OnLeftTONEvent())
                    clear()
                }
            }

            WORLD_PLAYER_LEFT_KEYWORD in log.msg -> {
                val player = log.msg.lastPath(WORLD_PLAYER_LEFT_KEYWORD).trim()
                val name = player.firstPath('(').trim()
                val id = player.middlePath('(', ')').trim()
                EventBus.publish(OnPlayerLeftRoomEvent(PlayerData(name, id, PlayerStatus.Left, null)))
            }

            log.name == BEHAVIOUR_KEYWORD && WORLD_PLAYER_JOINED_KEYWORD in log.msg -> {
                val player = log.msg.lastPath(WORLD_PLAYER_JOINED_KEYWORD).trim()
                val name = player.firstPath('(').trim()
                val id = player.middlePath('(', ')').trim()
                EventBus.publish(OnPlayerJoinedRoomEvent(PlayerData(name, id, PlayerStatus.Alive, null)))
            }

            ROUND_OVER_KEYWORD in log.msg -> {
                if (!isInTON.value) return
                EventBus.publish(OnRoundOverEvent(lastPrediction))
            }

            ROUND_WON_KEYWORD in log.msg -> {
                if (!isInTON.value) return
                EventBus.publish(OnRoundWonEvent())
            }

            ROUND_LOST_KEYWORD in log.msg -> {
                if (!isInTON.value) return
                EventBus.publish(OnRoundLostEvent())
            }

            ROUND_DEATH_KEYWORD in log.msg -> {
                if (!isInTON.value) return
                EventBus.publish(OnRoundDeathEvent())
            }

            log.name == PLAYER_DEATH_KEYWORD -> {
                if (!isInTON.value) return
                val name = log.msg.middlePath('[', ']').trim()
                val msg = log.msg.lastPath(']').trim()
                EventBus.publish(OnPlayerDeathEvent(PlayerData(name, null, PlayerStatus.Death, msg)))
            }

            TERROR_HUNGRY_HOME_INVADER_KEYWORD in log.msg -> {
                if (!isInTON.value || lastRoundType != RoundType.Classic) return
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE))
            }

            TERROR_ATRACHED_KEYWORD in log.msg -> {
                if (!isInTON.value || lastRoundType != RoundType.Classic) return
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE + 1))
            }

            TERROR_WILD_YET_BLOODTHIRSTY_CREATURE_KEYWORD in log.msg -> {
                if (!isInTON.value || lastRoundType != RoundType.Classic) return
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE + 2))
            }

            TERROR_TRANSPORTATION_TRIO_THE_DRIFTER_KEYWORD in log.msg -> {
                if (!isInTON.value || lastRoundType != RoundType.Unbound) return
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE))
            }

            TERROR_RED_MIST_APPARITION_KEYWORD in log.msg -> {
                if (!isInTON.value || lastRoundType != RoundType.EightPages) return
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE))
            }

            TERROR_BALDI_KEYWORD in log.msg -> {
                if (!isInTON.value || lastRoundType != RoundType.EightPages) return
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE + 1))
            }

            TERROR_SHADOW_FREDDY_KEYWORD in log.msg -> {
                if (!isInTON.value || lastRoundType != RoundType.EightPages) return
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE + 2))
            }

            TERROR_SEARCHLIGHTS_KEYWORD in log.msg -> {
                if (!isInTON.value || lastRoundType != RoundType.EightPages) return
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE + 3))
            }

            TERROR_ALTERNATES_KEYWORD in log.msg -> {
                if (!isInTON.value || lastRoundType != RoundType.EightPages) return
                EventBus.publish(OnHideTerrorShowUpEvent(Terror.HIDE + 4))
            }

            // https://github.com/ChrisFeline/ToNSaveManager/blob/main/Windows/MainWindow.cs
            KILLER_MATRIX_KEYWORD in log.msg || KILLER_MATRIX_UNKNOWN in log.msg || KILLER_MATRIX_REVEAL in log.msg -> {
                if (!isInTON.value) return
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
                
                EventBus.publish(OnKillerSetEvent(killerMatrix))
            }

            ROUND_TYPE_IS_KEYWORD in log.msg -> {
                if (!isInTON.value) return
                var possibleRoundType = log.msg.lastPath(ROUND_TYPE_IS_KEYWORD).trim()
                val possibleRoundTypeForPrint = possibleRoundType

                Logger.debug({ this::class.simpleName!! }, "possibleRoundType: '${possibleRoundType}'")

                possibleRoundType = possibleRoundType.jpToEn()

                if (possibleRoundType !in RoundTypeConvert.ENRoundTypes)
                    return

                val roundType = possibleRoundType.getTypeOfRound()

                if (!RoundTypeConvert.isCorrectGuess(lastPrediction, roundType)) {
                    wrongCount++

                    val isSpecialOrNot = roundType.isSpecialOrClassic()

                    Logger.debug(
                        { this::class.simpleName!! },
                        "Guess is wrong, Last Guess: $lastPrediction, Actually is: $isSpecialOrNot, Wrong Count: $wrongCount"
                    )
                }

                val mapString = log.msg.middlePath(ROUND_MAP_LOCATION_KEYWORD, AND_THE_ROUND_TYPE_IS_KEYWORD).trim()
                val idAndMap = mapString.split('(')
                val map = idAndMap[0].trim()
                val id = idAndMap[1].filter { it.isDigit() }.trim()
                
                EventBus.publish(OnRoundStartEvent(log.time, roundType, map, id.toInt()))

                updateRoundLog(roundType)
                Logger.info(
                    "log.new_round_started",
                    runCatching { RoundTypeConvert.getTextOfRound(roundType) }.getOrElse {
                        when (it) {
                            is UnknownRoundTypeException -> {
                                Logger.error(
                                    { this::class.simpleName!! },
                                    it,
                                    "exception.unknown_round_type",
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
                val prediction = predictNextRound()
                val recentRoundsLog = getRecentRoundsLog(ConfigManager.config.maxRecentRounds)

                lastPrediction = prediction
                EventBus.publish(OnNextPredictionEvent(prediction))

                Logger.info(
                    "log.next_round_should_be",
                    recentRoundsLog,
                    if (prediction == GuessRoundType.Special) special else classic
                )
                Logger.debug({ this::class.simpleName!! }, "Full Recent Rounds: ${getRecentRoundsLog(200)}")

                val sendValue = prediction == GuessRoundType.Special

                // Send OSC message
                OSCSender.send(sendValue)
                lastPredictionForOSC = sendValue
                lastRoundType = roundType
            }
        }

        if (roundLog.size >= ROUND_FAST_CHANGE && bonusFlag)
            bonusFlag = false
    }

    @Suppress("unused")
    fun read() = logInterpreter.read()

    suspend fun monitorRoundType() {
        while (true) {
            if (!VRChatWatcher.isVRChatRunning()) {
                Logger.info("log.vrchat_not_found")
                break
            }

            logInterpreter.read()
            delay(2000)
        }
    }
}