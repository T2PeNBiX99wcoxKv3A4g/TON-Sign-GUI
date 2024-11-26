package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import com.charleskorn.kaml.Yaml
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.ConfigScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventBus
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.OnConfigLoadedEvent
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.OnConfigStartSaveEvent
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.safeDecodeFromFile
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.debug
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.io.path.Path

object ConfigManager {
    private const val FILE_NAME = "config.yml"
    private val filePath = Path(Utils.currentWorkingDirectory, FILE_NAME)
    private val fileBakPath = Path(Utils.currentWorkingDirectory, "$FILE_NAME.bak")

    val Default = Config(
        language = "en",
        maxRecentRounds = 7,
        autoSaveMinutes = 30f,
        roundNotify = true,
        roundNotifyOnlySpecial = true,
        playerJoinedNotify = true,
        playerLeftNotify = true,
        autoScrollToDown = true,
        onTop = false
    )

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

        ConfigScope.launch {
            autoSave()
        }
    }

    private fun load() {
        _config = Yaml.default.safeDecodeFromFile<Config>(file, Default.copy(), {
            val text = "exception.config_load_error".i18nByLang("en", it.localizedMessage ?: "Unknown")
            Utils.logger.error(it) { "[${this::class.simpleName}] $text" }
        }) {
            Utils.logger.error(it) { "[${this::class.simpleName}] Config fix failed: ${(it.localizedMessage)}" }
            renameFile()
        }
        save()
        EventBus.publish(OnConfigLoadedEvent(config))
    }

    fun save() {
        EventBus.publish(OnConfigStartSaveEvent(config))
        file.writeText(Yaml.default.encodeToString(Config.serializer(), config))
    }

    private fun renameFile() {
        if (!file.exists()) return
        val fileBakFile = fileBakPath.toFile()

        if (fileBakFile.exists())
            fileBakFile.renameTo(Path(Utils.currentWorkingDirectory, "$FILE_NAME.${Utils.timeNowForFile}.bak").toFile())

        file.renameTo(fileBakFile)
    }

    private suspend fun autoSave() {
        while (true) {
            delay((config.autoSaveMinutes * 60 * 1000).toLong())
            save()
            Logger.debug<ConfigManager> { "Auto save" }
        }
    }
}