package io.github.t2penbix99wcoxkv3a4g.tonsign.tes

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher
import kotlin.io.path.Path

fun main() {
    val file = Path(System.getProperty("user.home"), "test.log").toFile()
    Utils.logger.debug { "$file, ${file.exists()}" }
//    val watcher = LogWatcher(file)
    val watcher = LogWatcher.Default
    watcher.read()
}