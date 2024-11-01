package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.interpreter.LogEvent
import io.github.t2penbix99wcoxkv3a4g.tonsign.interpreter.LogInterpreter
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher

fun main() {
    val interpreter = LogInterpreter(LogWatcher.findLatestLog())
    interpreter.onReadLogEvent += ::testReadLog
    interpreter.read()
}

private fun testReadLog(log: LogEvent) {
    Utils.logger.debug { "Time: ${log.time}, Level: ${log.level}, Name: ${log.name}, Msg: ${log.msg}" }
}