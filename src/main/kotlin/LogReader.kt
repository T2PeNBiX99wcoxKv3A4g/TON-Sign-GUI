import roundType.GuessRoundType
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

    fun monitorRoundType() {
        while (true) {
            break
        }
    }
}