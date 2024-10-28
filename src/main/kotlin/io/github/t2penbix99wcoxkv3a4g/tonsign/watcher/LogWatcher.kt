package io.github.t2penbix99wcoxkv3a4g.tonsign.watcher

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.github.t2penbix99wcoxkv3a4g.tonsign.OSCSender
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.Event
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventArg
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.readLineUTF8
import io.github.t2penbix99wcoxkv3a4g.tonsign.exception.UnknownRoundTypeException
import io.github.t2penbix99wcoxkv3a4g.tonsign.exception.WrongRecentRoundException
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RandomRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundTypeConvert
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile

class LogWatcher(val logFile: File) {
    companion object {
        val Default: LogWatcher
            get() = LogWatcher(findLatestLog())

        fun findLatestLog(): File {
            val files = Utils.logDirectory.toFile().listFiles { file, filename ->
                filename.endsWith(".txt")
            }

            if (files.size < 1)
                throw FileNotFoundException(LanguageManager.get("exception.no_log_file"))

            files.sortWith { f1, f2 ->
                val compare = f1.lastModified() > f2.lastModified()
                if (compare)
                    return@sortWith -1
                else
                    return@sortWith 1
                return@sortWith 0
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
            "Memory Usage: after world loaded [wrld_a61cdabe-1218-4287-9ffc-2a4d1414e5bd]"
        private const val WORLD_LEFT_KEYWORD = "OnLeftRoom"
        private const val RANDOM_COUNT_CHANGE = 1
        private const val RANDOM_COUNT_RESET = 3
    }

    private val roundLog = mutableListOf<GuessRoundType>()
    private var lastPosition = 0L
    private var lastPredictionForOSC = false
    private var lastPrediction = GuessRoundType.NIL
    private var bonusFlag = false
    private var randomRound = RandomRoundType.Unknown
    private var randomCount = 0
    private var lastRandomCount = -1
    private var wrongCount = 0
    private var isTONLoaded = false

    val onNextPredictionEvent = EventArg<GuessRoundType>()
    val onRoundOverEvent = EventArg<GuessRoundType>()
    val onReadLineEvent = EventArg<String>()
    val onJoinTONEvent = Event()
    val onLeftTONEvent = Event()
    val isInTON = mutableStateOf(false)

    private val maxRoundChange: Int
        get() {
            return when (randomRound) {
                RandomRoundType.FAST -> ROUND_FAST_CHANGE
                RandomRoundType.NORMAL -> ROUND_NORMAL_CHANGE
                else -> ROUND_NORMAL_CHANGE
            }
        }

    fun isAlternatePattern(): Boolean {
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

    fun predictNextRound(): GuessRoundType {
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
                val last = roundLog.takeLast(3)
                if (last.size > 2 && last[0] == last[1] || last[1] == last[2]) {
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
            val last = roundLog.takeLast(2)
            return if (last[0] == GuessRoundType.Classic && last[1] == GuessRoundType.Classic) GuessRoundType.Special else GuessRoundType.Classic
        }
    }

    fun getRecentRoundsLog(maxRecent: Int): String {
        return roundLog.takeLast(maxRecent).joinToString {
            when (it) {
                GuessRoundType.Classic -> LanguageManager.get("log.recent_rounds_log_classic")
                GuessRoundType.Special -> LanguageManager.get("log.recent_rounds_log_special")
                else -> throw WrongRecentRoundException(LanguageManager.get("exception.wrong_recent_round", it))
            }
        }
    }

    private val _getRecentRoundsLogState = mutableStateOf("")

    val getRecentRoundsLogState: MutableState<String>
        get() {
            _getRecentRoundsLogState.value = getRecentRoundsLog(ConfigManager.config.maxRecentRounds)
            return _getRecentRoundsLogState
        }

    fun clear() {
        roundLog.clear()
        lastPosition = 0L
        lastPredictionForOSC = false
        lastPrediction = GuessRoundType.NIL
        bonusFlag = false
        randomRound = RandomRoundType.Unknown
        randomCount = 0
    }

    private fun updateRoundLog(round: RoundType) {
        var classification = RoundTypeConvert.classifyRound(round)

        if (classification == GuessRoundType.Exempt && roundLog.size >= 2) {
            val last = roundLog.takeLast(2)
            when {
                last[0] == GuessRoundType.Classic && last[1] == GuessRoundType.Classic -> classification =
                    GuessRoundType.Special

                last[0] == GuessRoundType.Classic && last[1] == GuessRoundType.Special -> classification =
                    GuessRoundType.Classic

                last[0] == GuessRoundType.Special && last[1] == GuessRoundType.Classic -> classification =
                    if (isAlternatePattern()) GuessRoundType.Special else GuessRoundType.Classic
            }
        }

        roundLog.add(classification)
    }

    private fun readLine(line: String) {
        onReadLineEvent(line)
        when {
            // TERROR NIGHTS STRING
            BONUS_ACTIVE_KEYWORD in line -> {
                bonusFlag = true
                Logger.info("log.think_terror_nights")
            }

            MASTER_CLIENT_SWITCHED_KEYWORD in line -> {
                Logger.info("log.host_just_left")
                clear()
                OSCSender.send(true)
                lastPredictionForOSC = true
            }

            SAVING_AVATAR_DATA_KEYWORD in line -> {
                Logger.info("log.saving_avatar_data")
                OSCSender.send(lastPredictionForOSC)
            }

            WORLD_JOIN_KEYWORD in line -> {
                isTONLoaded = true
                isInTON.value = true
                Logger.info("log.is_join_ton")
                onJoinTONEvent()
            }

            WORLD_LEFT_KEYWORD in line -> {
                if (isTONLoaded) {
                    isInTON.value = false
                    Logger.info("log.is_left_ton")
                    onLeftTONEvent()
                    clear()
                }
            }

            ROUND_OVER_KEYWORD in line -> {
                onRoundOverEvent(lastPrediction)
            }

            ROUND_TYPE_IS_KEYWORD in line -> {
                val parts = line.split(ROUND_TYPE_IS_KEYWORD)

                if (parts.size < 2)
                    return

                val path = parts[1]
                var possibleRoundType = path.substring(1, path.length)
                val possibleRoundTypeForPrint = possibleRoundType

                Logger.debug({ this::class.simpleName!! }, "possibleRoundType: '${possibleRoundType}'")

                if (possibleRoundType in RoundTypeConvert.JPRoundTypes)
                    possibleRoundType =
                        RoundTypeConvert.ENRoundTypes[RoundTypeConvert.JPRoundTypes.indexOf(possibleRoundType)]

                if (possibleRoundType !in RoundTypeConvert.ENRoundTypes)
                    return

                val roundType = RoundTypeConvert.getTypeOfRound(possibleRoundType)

                if (!RoundTypeConvert.isCorrectGuess(lastPrediction, roundType)) {
                    wrongCount++

                    val isSpecialOrNot = RoundTypeConvert.isSpecialOrClassic(roundType)

                    Logger.debug(
                        { this::class.simpleName!! },
                        "Guess is wrong, Last Guess: $lastPrediction, Actually is: $isSpecialOrNot, Wrong Count: $wrongCount"
                    )
                }

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
                                LanguageManager.get(
                                    "exception.unknown_round_type_simple",
                                    possibleRoundTypeForPrint
                                )
                            }

                            else -> throw it
                        }
                    }
                )

                val classic = LanguageManager.get("log.predict_next_round_classic")
                val special = LanguageManager.get("log.predict_next_round_special")
                val prediction = predictNextRound()
                val recentRoundsLog = getRecentRoundsLog(ConfigManager.config.maxRecentRounds)

                lastPrediction = prediction
                onNextPredictionEvent(prediction)

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
            }
        }
    }

    fun read() {
        val raf = RandomAccessFile(logFile, "r")
        val length = raf.length()
        raf.seek(lastPosition)
        var charPosition = raf.filePointer

        while (charPosition < length) {
            val line = raf.readLineUTF8()
            readLine(line)
            charPosition = raf.filePointer

            if (roundLog.size >= ROUND_FAST_CHANGE && bonusFlag)
                bonusFlag = false
        }

        lastPosition = charPosition
    }

    suspend fun monitorRoundType() {
        while (true) {
            if (!VRChatWatcher.isVRChatRunning()) {
                Logger.info("log.vrchat_not_found")
                break
            }

            read()
            delay(2000)
        }
    }
}