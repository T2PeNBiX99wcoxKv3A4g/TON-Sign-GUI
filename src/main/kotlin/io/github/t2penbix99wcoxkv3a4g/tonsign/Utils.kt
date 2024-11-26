package io.github.t2penbix99wcoxkv3a4g.tonsign

import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.Path

object Utils {
    const val TITLE = "Ton Sign"
    const val ID = "TonSign"
    private const val VERSION_FILE = "/version.properties"

    private val versionProperties = Properties()

    val logger = KotlinLogging.logger(ID)

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

    val version: String
        get() = versionProperties.getProperty("version") ?: "0.0.0"

    fun resourceUrl(name: String): URL? = this.javaClass.getResource(name)

    fun resourceFile(name: String): File? {
        val url = resourceUrl(name) ?: return null
        return File(url.file)
    }

    fun resourceAsStream(name: String): InputStream? = this.javaClass.getResourceAsStream(name)

    init {
        versionProperties.load(resourceAsStream(VERSION_FILE))
    }
}