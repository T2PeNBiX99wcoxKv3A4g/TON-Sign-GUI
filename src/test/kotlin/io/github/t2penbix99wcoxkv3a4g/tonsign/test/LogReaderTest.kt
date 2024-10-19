package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.LogReader
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import kotlin.io.path.Path
import kotlin.io.path.pathString

fun main() {
    val file = Path(Utils.logDirectory.pathString, "output_log_2024-10-17_21-48-03.txt").toFile()
    val logReader = LogReader(file)
    logReader.read()
}