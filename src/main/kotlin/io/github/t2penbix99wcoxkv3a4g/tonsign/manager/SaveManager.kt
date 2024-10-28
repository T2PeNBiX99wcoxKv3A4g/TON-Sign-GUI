package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import com.charleskorn.kaml.Yaml
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.ConfigScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventArg
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.safeDecodeFromFile
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.safeFormat
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.RoundData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.io.path.Path

object SaveManager {
    private const val fileName = "save.yml"
    private val scope = ConfigScope()
    private val filePath = Path(Utils.currentWorkingDirectory, fileName)
    private val fileBakPath = Path(Utils.currentWorkingDirectory, "$fileName.bak")

    val Default = Save(listOf<RoundData>())
    val onLoadedSaveEvent = EventArg<Save>()
    val onStartSaveEvent = EventArg<Save>()

    private var _save: Save? = null
    var save: Save
        get() {
            if (_save == null)
                load()
            return _save!!
        }
        set(value) {
            _save = value
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
        _save = Yaml.default.safeDecodeFromFile<Save>(file, Default.copy(), {
            val text =
                LanguageManager.getByLang("en", "exception.config_load_error").safeFormat(it.message ?: "Unknown")
            Utils.logger.error(it) { "[${this::class.simpleName!!}] $text" }
        }) {
            Utils.logger.error(it) { "[${this::class.simpleName!!}] Config auto fix failed: ${it.message}" }
            renameFile()
        }
        save()
        onLoadedSaveEvent(save)
    }

    fun save() {
        onStartSaveEvent(save)
        file.writeText(Yaml.default.encodeToString(Save.serializer(), save))
    }

    private fun renameFile() {
        if (!file.exists()) return
        val fileBakFile = fileBakPath.toFile()

        if (fileBakFile.exists())
            fileBakFile.renameTo(
                Path(
                    Utils.currentWorkingDirectory,
                    "${fileName}.${Utils.timeNowForFile}.bak"
                ).toFile()
            )

        file.renameTo(fileBakFile)
    }

    private suspend fun autoSave() {
        while (true) {
            delay((ConfigManager.config.autoSaveMinutes * 60 * 1000).toLong())
            save()
            Logger.debug({ this::class.simpleName!! }, "Auto save")
        }
    }
}