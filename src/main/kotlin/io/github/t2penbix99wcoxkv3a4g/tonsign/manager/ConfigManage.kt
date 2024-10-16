package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import com.charleskorn.kaml.MissingRequiredPropertyException
import com.charleskorn.kaml.UnknownPropertyException
import com.charleskorn.kaml.Yaml
import io.github.t2penbix99wcoxkv3a4g.tonsign.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path

object ConfigManage {
    private const val fileName = "config.yml"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val filePath = Path(Utils.currentWorkingDirectory, fileName)
    private val fileBakPath = Path(Utils.currentWorkingDirectory, "$fileName.bak")

    val Default = Config("en", 7, 30f)

    private var _config: Config? = null
    val config: Config
        get() {
            if (_config == null)
                load()
            return _config!!
        }

    private var _file: File? = null
    val file: File
        get() {
            if (_file == null)
                _file = filePath.toFile()
            return _file!!
        }

    init {
        load()

        scope.launch {
            autoSave()
        }
    }

    fun load() {
        if (!file.exists()) {
            file.writeText(Yaml.default.encodeToString(Config.serializer(), Default))
            return
        }

        _config = runCatching { Yaml.default.decodeFromString<Config>(file.readText()) }.getOrElse {
            when (it) {
                is UnknownPropertyException,
                is MissingRequiredPropertyException -> {
                    Logger.error(it, "Config Load Error: ${it.message}")
                    renameFile()
                    return@getOrElse Default
                }

                else -> throw it
            }
        }
    }

    fun save() {
        file.writeText(Yaml.default.encodeToString(Config.serializer(), config))
    }

    private fun renameFile() {
        val fileBakFile = fileBakPath.toFile()

        if (fileBakFile.exists())
            fileBakFile.renameTo(Path(Utils.currentWorkingDirectory, "$fileName.${Utils.timeNowForFile}").toFile())

        file.renameTo(fileBakFile)
    }

    private suspend fun autoSave() {
        while (true) {
            delay((config.autoSaveMinutes * 60 * 1000).toLong())
            save()
        }
    }
}