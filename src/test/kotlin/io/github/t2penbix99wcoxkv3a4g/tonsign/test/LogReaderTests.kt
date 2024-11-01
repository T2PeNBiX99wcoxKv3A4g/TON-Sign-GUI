package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly

@TestOnly
object LogReaderTest {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun readFileTest() {
//        val file = Path(Utils.logDirectory.pathString, "output_log_2024-10-19_02-43-26.txt").toFile()
//        val logWatcher = LogWatcher(file)
//        logWatcher.read()
    }

    fun runningTest() {
        val logWatcher = LogWatcher.Default

        scope.launch {
            logWatcher.monitorRoundType()
        }
    }
}

fun main() {
    LogReaderTest.runningTest()
}