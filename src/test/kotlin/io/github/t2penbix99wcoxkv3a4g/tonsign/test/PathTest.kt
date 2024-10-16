package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import kotlin.io.path.Path

fun main() {
    val path = Path(Utils.currentWorkingDirectory, "config.yml")
    val file = path.toFile()

    Utils.logger.info { "Test $path $file ${file.exists()}" }
}