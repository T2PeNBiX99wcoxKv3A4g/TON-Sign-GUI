package io.github.t2penbix99wcoxkv3a4g.tonsign

import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Path
import kotlin.io.path.Path

object Utils {
    const val TITLE = "Ton Sign"
    
    val logger = KotlinLogging.logger("TonSign")
    
    val logDirectory: Path
        get() {
            return Path(System.getProperty("user.home"), "AppData", "LocalLow", "VRChat", "VRChat")
        }
}