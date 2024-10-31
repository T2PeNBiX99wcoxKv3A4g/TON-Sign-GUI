package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import com.charleskorn.kaml.Yaml
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.ConfigScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventArg
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.safeDecodeFromFile
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.safeFormat
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.io.path.Path

object ConfigManager {
    private const val fileName = "config.yml"
    private val scope = ConfigScope()
    private val filePath = Path(Utils.currentWorkingDirectory, fileName)
    private val fileBakPath = Path(Utils.currentWorkingDirectory, "$fileName.bak")

    val Default = Config("en", 7, 30f, true, true, false)
    val onConfigLoaded = EventArg<Config>()

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
        _config = Yaml.default.safeDecodeFromFile<Config>(file, Default.copy(), {
            val text = "exception.config_load_error".i18nByLang("en", it.message ?: "Unknown")
            Utils.logger.error(it) { "[${this::class.simpleName!!}] $text" }
        }) {
            Utils.logger.error(it) { "[${this::class.simpleName!!}] Config fix failed: ${(it.message)}" }
            renameFile()
        }
        save()
        onConfigLoaded(config)
    }

    fun save() {
        file.writeText(Yaml.default.encodeToString(Config.serializer(), config))
    }

    private fun renameFile() {
        if (!file.exists()) return
        val fileBakFile = fileBakPath.toFile()

        if (fileBakFile.exists())
            fileBakFile.renameTo(Path(Utils.currentWorkingDirectory, "$fileName.${Utils.timeNowForFile}.bak").toFile())

        file.renameTo(fileBakFile)
    }

    private suspend fun autoSave() {
        while (true) {
            delay((config.autoSaveMinutes * 60 * 1000).toLong())
            save()
            Logger.debug({ this::class.simpleName!! }, "Auto save")
        }
    }
}