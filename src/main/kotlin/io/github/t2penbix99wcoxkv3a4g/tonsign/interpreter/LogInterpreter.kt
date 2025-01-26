package io.github.t2penbix99wcoxkv3a4g.tonsign.interpreter

import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventBus
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.OnReadLogEvent
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.firstPath
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.lastPath
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.middlePath
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.readLineUTF8
import java.io.File
import java.io.RandomAccessFile
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class LogInterpreter(private val logFile: File) {
    private companion object {
        private const val FIRST_KEYWORD = "  -  "
        private const val DEBUG_KEYWORD = "Debug"
        private const val WARNING_KEYWORD = "Warning"
        private const val ERROR_KEYWORD = "Error"
        private const val EXCEPTION_KEYWORD = "Exception"
    }

    private var lastPosition = 0L
    private val logs = mutableListOf<LogEvent>()
    private val lastLog: LogEvent?
        get() {
            if (logs.isEmpty())
                return null
            return logs[logs.size - 1]
        }

    private fun getLogLevel(level: String): LogLevel {
        return when (level) {
            DEBUG_KEYWORD -> LogLevel.DEBUG
            WARNING_KEYWORD -> LogLevel.WARN
            ERROR_KEYWORD -> LogLevel.ERROR
            EXCEPTION_KEYWORD -> LogLevel.EXCEPTION
            else -> LogLevel.INFO
        }
    }

    private fun readLine(line: String) {
        when {
            FIRST_KEYWORD in line -> {
                val timeLevelPath = line.firstPath(FIRST_KEYWORD).trim().split(' ')
                val msgPath = line.lastPath(FIRST_KEYWORD).trim()
                val date = timeLevelPath[0].split('.')
                val year = date[0]
                val month = date[1]
                val day = date[2]
                val time = timeLevelPath[1].split(':')
                val hour = time[0]
                val minute = time[1]
                val second = time[2]
                val level = getLogLevel(timeLevelPath[2])
                val timeZone =
                    LocalDateTime.of(LocalDate.parse("$year-$month-$day"), LocalTime.parse("$hour:$minute:$second"))
                        .atZone(ZoneId.systemDefault())

                if (logs.size > 1)
                    EventBus.publish(OnReadLogEvent(lastLog!!))

                val msgPathOne = if (msgPath.isNotEmpty()) msgPath[0] else ""

                if (msgPathOne == '[') {
                    val name = line.middlePath('[', ']').trim()
                    val msg = line.lastPath(']').trim()

                    logs.add(LogEvent(timeZone, level, name, msg))
                } else
                    logs.add(LogEvent(timeZone, level, null, msgPath))
            }

            else -> {
                if (logs.isEmpty() || line.isEmpty()) return
                logs[logs.size - 1] = lastLog!!.copy(msg = "${lastLog!!.msg}\n$line")
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
        }

        lastPosition = charPosition
    }
}