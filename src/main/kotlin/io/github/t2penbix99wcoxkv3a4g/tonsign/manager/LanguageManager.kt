@file:Suppress("unused")

package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.yamlMap
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.firstPath
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.safeFormat
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.sha256
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.toFile
import io.github.t2penbix99wcoxkv3a4g.tonsign.exception.FolderNotFoundException
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.error
import java.io.File
import kotlin.io.path.Path

object LanguageManager {
    private const val DIR = "language"
    private const val YML = ".yml"
    private const val SHA256 = ".sha256"
    private val dataBase = mutableMapOf<String, YamlMap>()
    private val states = mutableMapOf<String, MutableState<String>>()
    private val langState = mutableStateOf("")
    private val language: String
        get() = runCatching { ConfigManager.config.language }.getOrElse { "en" }
    private val internalLanguageList = listOf(
        "en",
        "jp"
    )

    @Suppress("unused")
    val lang: MutableState<String>
        get() = langState

    @Suppress("MemberVisibilityCanBePrivate")
    val dir: File = Path(Utils.currentWorkingDirectory, DIR).toFile()

    init {
        checkDir()
        updateLanguage()
        load()
    }

    private fun checkDir() {
        if (!dir.exists())
            dir.mkdir()

        internalLanguageList.forEach {
            val file = Path(Utils.currentWorkingDirectory, DIR, "$it$YML").toFile()
            if (file.exists()) return@forEach
            val stream = Utils.resourceAsStream("/$DIR/$it$YML")
            requireNotNull(stream)
            stream.toFile(file.path)
        }
    }

    private fun updateLanguage() {
        if (!dir.exists()) return

        internalLanguageList.forEach {
            val file = Path(Utils.currentWorkingDirectory, DIR, "$it$YML").toFile()
            if (!file.exists()) return@forEach
            val sha256Stream = Utils.resourceAsStream("/$DIR/$it$YML$SHA256")
            requireNotNull(sha256Stream)
            val reader = sha256Stream.reader(charset = Charsets.UTF_8)
            val sha256FromJar = reader.readText().firstPath(' ')
            val sha256FromFile = file.readText(charset = Charsets.UTF_8).sha256()
            if (sha256FromFile != sha256FromJar) {
                val stream = Utils.resourceAsStream("/$DIR/$it$YML")
                requireNotNull(stream)
                stream.toFile(file.path)
            }
        }
    }

    private fun load() {
        runCatching {
            if (!dir.exists()) {
                Logger.error<LanguageManager>(FolderNotFoundException("'${dir.path}' is not exists!")) { "Can't found 'language' folder! Language is not working anymore." }
                return
            }
            dir.listFiles { _, filename -> filename.endsWith(YML) }?.forEach {
                val data = Yaml.default.parseToYamlNode(it.readText())
                val langID = it.name.firstPath('.')

                dataBase[langID] = data.yamlMap
            }
        }.getOrElse {
            Logger.error<LanguageManager>(it, { "Initialization Failure: %s" }, it.message ?: "Unknown")
        }
    }

    fun exists(text: String): Boolean {
        if (language in dataBase && dataBase[language]?.get<YamlScalar>(text) != null)
            return true

        if ("en" in dataBase && dataBase["en"]?.get<YamlScalar>(text) != null)
            return true
        return false
    }

    fun exists(lang: String, text: String) = lang in dataBase && dataBase[lang]?.get<YamlScalar>(text) != null

    @Suppress("MemberVisibilityCanBePrivate")
    fun existsLanguage(lang: String) = lang in dataBase

    fun getByLang(lang: String, text: String): String {
        if (lang !in dataBase || dataBase[lang]?.get<YamlScalar>(text) == null) {
            if ("en" in dataBase && dataBase["en"]?.get<YamlScalar>(text) != null)
                return dataBase["en"]?.get<YamlScalar>(text)!!.content
            return text
        }
        return dataBase[lang]!!.get<YamlScalar>(text)!!.content
    }

    fun getByLang(lang: String, text: String, vararg objects: Any?) = getByLang(lang, text).safeFormat(*objects)
    fun get(text: String, vararg objects: Any?) = getByLang(language, text, *objects)

    fun getWithEn(text: String, vararg objects: Any?): String {
        if (!exists(language, text) || language == "en" || get(text, *objects) == getByLang("en", text, *objects))
            return getByLang("en", text, *objects)
        return "${get(text, *objects)} (${getByLang("en", text, *objects)})"
    }

    fun getState(text: String, vararg objects: Any?): MutableState<String> {
        if (!exists(text))
            return mutableStateOf(get(text, *objects))
        if (text !in states)
            states[text] = mutableStateOf(get(text, *objects))
        else
            states[text]!!.value = get(text, *objects)
        return states[text]!!
    }

    fun getStateWithEn(text: String, vararg objects: Any?): MutableState<String> {
        if (!exists(language, text) || language == "en" || get(text, *objects) == getByLang("en", text, *objects))
            return mutableStateOf(getByLang("en", text, *objects))
        if (text !in states)
            states[text] = mutableStateOf(getWithEn(text, *objects))
        else
            states[text]!!.value = getWithEn(text, *objects)
        return states[text]!!
    }

    fun setLanguage(lang: String) {
        if (!existsLanguage(lang)) return
        runCatching { ConfigManager.config.language = lang }
        langState.value = lang
    }
}