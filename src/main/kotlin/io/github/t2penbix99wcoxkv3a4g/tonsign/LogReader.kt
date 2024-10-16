package io.github.t2penbix99wcoxkv3a4g.tonsign

import io.github.t2penbix99wcoxkv3a4g.tonsign.exception.WrongRecentRoundException
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManage
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundTypeConvert
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.VRChatWatcher
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile

class LogReader(val logFile: File) {
    companion object {
        val Default: LogReader
            get() = LogReader(findLatestLog())

        fun findLatestLog(): File {
            val test = Utils.logDirectory.toFile().listFiles { file, filename ->
                return@listFiles filename.endsWith(".txt")
            }

            if (test.size < 1)
                throw FileNotFoundException(LanguageManager.get("exception.no_log_file"))

            test.sortWith { f1, f2 ->
                val compare = f1.lastModified() > f2.lastModified()
                if (compare)
                    return@sortWith -1
                else
                    return@sortWith 1
                return@sortWith 0
            }

            return test.first()
        }
    }

    val roundLog = mutableListOf<GuessRoundType>()
    var lastPosition = 0L
    var lastPrediction = false
    var bonusFlag = false

    fun isAlternatePattern(): Boolean {
        return roundLog.takeLast(6).count { it == GuessRoundType.Special } > 2
    }

    fun predictNextRound(): GuessRoundType {
        if (roundLog.size < 2)
            return GuessRoundType.Classic

        val last = roundLog.takeLast(2)

        if (last[0] == GuessRoundType.Special && last[0] == GuessRoundType.Special) {
            Logger.info("log.host_left_before")
            roundLog.removeLast()
        }

        if (isAlternatePattern() || bonusFlag)
            return if (roundLog.last() == GuessRoundType.Special) GuessRoundType.Classic else GuessRoundType.Special
        else {
            val last = roundLog.takeLast(2)
            return if (last[0] == GuessRoundType.Classic && last[1] == GuessRoundType.Classic) GuessRoundType.Special else GuessRoundType.Classic
        }
    }
    
    fun getRecentRoundsLog(): String {
        return roundLog.joinToString {
            when (it) {
                GuessRoundType.Classic -> return@joinToString LanguageManager.get("log.recent_rounds_log_classic")
                GuessRoundType.Special -> return@joinToString LanguageManager.get("log.recent_rounds_log_special")
                else -> throw WrongRecentRoundException(LanguageManager.get("exception.wrong_recent_round", it))
            }
        }
    }

    fun updateRoundLog(round: RoundType) {
        var classification = RoundTypeConvert.classifyRound(round)

        if (classification == GuessRoundType.Exempt && roundLog.size >= 2) {
            val last = roundLog.takeLast(2)
            if (last[0] == GuessRoundType.Classic && last[1] == GuessRoundType.Classic)
                classification = GuessRoundType.Special
            else if (last[0] == GuessRoundType.Classic && last[1] == GuessRoundType.Special)
                classification = GuessRoundType.Classic
            else if (last[0] == GuessRoundType.Special && last[1] == GuessRoundType.Classic)
                classification = if (isAlternatePattern()) GuessRoundType.Special else GuessRoundType.Classic
        }

        roundLog.add(classification)

        if (roundLog.size > ConfigManage.config.maxRecentRounds)
            roundLog.removeAt(0)
    }

    fun readLine(line: String) {
        // TERROR NIGHTS STRING
        if ("BONUS ACTIVE!" in line) {
            bonusFlag = true
            Logger.info("log.think_terror_nights")
        } else if ("OnMasterClientSwitched" in line) {
            Logger.info("log.host_just_left")
            OSCSender.send(true)
            lastPrediction = true
        } else if ("Saving Avatar Data:" in line) {
            Logger.info("log.saving_avatar_data")
            OSCSender.send(lastPrediction)
        } else if ("round type is" in line) {
            val parts = line.split("round type is")

            if (parts.size > 1) {
                val path = parts[1]
                var possibleRoundType = path.substring(1, path.length - 1) // TODO: Not sure
                val possibleRoundTypeForPrint = possibleRoundType

                Logger.debug("possible_round_type '${possibleRoundType}'")

                if (possibleRoundType in RoundTypeConvert.JPRoundTypes)
                    possibleRoundType =
                        RoundTypeConvert.ENRoundTypes[RoundTypeConvert.JPRoundTypes.indexOf(possibleRoundType)]

                if (possibleRoundType in RoundTypeConvert.ENRoundTypes) {
                    val roundType = RoundTypeConvert.getTypeOfRound(possibleRoundType)

                    updateRoundLog(roundType)
                    Logger.info(
                        "log.new_round_started",
                        RoundTypeConvert.getTextOfRound(roundType, possibleRoundTypeForPrint)
                    )

                    val classic = LanguageManager.get("log.predict_next_round_classic")
                    val special = LanguageManager.get("log.predict_next_round_special")
                    val prediction = predictNextRound()
                    val recentRoundsLog = getRecentRoundsLog()

                    Logger.info(
                        "log.next_round_should_be",
                        recentRoundsLog,
                        if (prediction == GuessRoundType.Special) special else classic
                    )

                    // Send OSC message
                    if (prediction == GuessRoundType.Special) {
                        OSCSender.send(true)
                        lastPrediction = true
                    } else {
                        OSCSender.send(false)
                        lastPrediction = false
                    }
                }
            }
        }
    }

    suspend fun monitorRoundType() {
        while (true) {
            if (!VRChatWatcher.isVRChatRunning()) {
                Logger.info("log.vrchat_not_found")
                break
            }

            val raf = RandomAccessFile(logFile, "r")
            val length = raf.length()
            raf.seek(lastPosition)
            var charPosition = raf.filePointer

            while (charPosition < length) {
                val line = raf.readLine()
                readLine(line)
                charPosition = raf.filePointer

                if (roundLog.size >= 6 && bonusFlag)
                    bonusFlag = false
            }

            lastPosition = charPosition
            delay(2000)
        }
    }
}