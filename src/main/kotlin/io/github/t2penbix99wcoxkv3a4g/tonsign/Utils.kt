package io.github.t2penbix99wcoxkv3a4g.tonsign

import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path

object Utils {
    const val TITLE = "Ton Sign"

    val logger = KotlinLogging.logger("TonSign")

    val logDirectory: Path
        get() = Path(System.getProperty("user.home"), "AppData", "LocalLow", "VRChat", "VRChat")

    val currentWorkingDirectory: String
        get() = System.getProperty("user.dir")

    val timeNowForFile: String
        get() {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")
            return current.format(formatter)
        }
}