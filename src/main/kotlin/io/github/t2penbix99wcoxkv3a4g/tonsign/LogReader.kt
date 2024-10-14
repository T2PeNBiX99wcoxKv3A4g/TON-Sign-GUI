import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundTypeConvert
import java.io.File
import kotlin.io.path.pathString

class LogReader(logFile: File) {
    companion object {
        private var _instance: LogReader? = null

        val Default: LogReader
            get() {
                if (_instance == null)
                    _instance = LogReader(findLatestLog())
                return _instance!!
            }

        fun findLatestLog(): File {
            val test = File(Utils.logDirectory.pathString).listFiles { file, filename ->
                return@listFiles filename.endsWith(".txt")
            }

            if (test.size < 1) {
                // TODO: file not find
//                lm.error("log.no_log_file")
                throw Exception()
            }

            test.sortWith { f1, f2 ->
                val compare = f1.lastModified() > f2.lastModified()
                if (compare)
                    return@sortWith -1
                else
                    return@sortWith 1
                return@sortWith 0
            }

            Utils.logger.info { "Latest File: ${test.first()}" }
            return test.first()
        }
    }

    val roundLog = mutableListOf<GuessRoundType>()
    var lastPosition = 0
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
//            lm.info("log.host_left_before") TODO: log info
            roundLog.removeLast()
        }

        if (isAlternatePattern() || bonusFlag)
            return if (roundLog.last() == GuessRoundType.Special) GuessRoundType.Classic else GuessRoundType.Special
        else {
            val last = roundLog.takeLast(2)
            return if (last[0] == GuessRoundType.Classic && last[1] == GuessRoundType.Classic) GuessRoundType.Special else GuessRoundType.Classic
        }
    }

    fun getRecentRoundsLog() {
        
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

        // TODO: Config max size
        if (roundLog.size > 7)
            roundLog.removeAt(0)
    }

    fun monitorRoundType() {
        while (true) {
            break
        }
    }
}