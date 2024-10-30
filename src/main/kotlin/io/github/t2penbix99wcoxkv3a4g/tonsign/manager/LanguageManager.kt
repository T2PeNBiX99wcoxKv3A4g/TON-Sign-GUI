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
import io.github.t2penbix99wcoxkv3a4g.tonsign.exception.FolderNotFoundException
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import kotlin.io.path.Path

object LanguageManager {
    private const val DIR = "language"
    private val dataBase = mutableMapOf<String, YamlMap>()
    private val states = mutableMapOf<String, MutableState<String>>()
    private val langState = mutableStateOf("")
    private val language: String
        get() = runCatching { ConfigManager.config.language }.getOrElse { "en" }

    @Suppress("unused")
    val lang: MutableState<String>
        get() = langState

    init {
        load()
    }

    fun load() {
        runCatching {
            val dir = Path(Utils.currentWorkingDirectory, DIR).toFile()
            if (!dir.exists()) {
                Logger.error(
                    { this::class.simpleName!! },
                    FolderNotFoundException("'${dir.path}' is not exists!"),
                    "Can't found 'language' folder! Language is not working anymore."
                )
                return
            }
            dir.listFiles { file, filename -> filename.endsWith(".yml") }.forEach {
                val data = Yaml.default.parseToYamlNode(it.readText())
                val langID = it.name.firstPath('.')

                dataBase[langID] = data.yamlMap
            }
        }.getOrElse {
            Logger.error({ this::class.simpleName!! }, it, "Initialization Failure: %s", it.message ?: "Unknown")
        }
    }

    fun exists(text: String): Boolean {
        if (language in dataBase && dataBase[language]!!.get<YamlScalar>(text) != null)
            return true

        if ("en" in dataBase && dataBase["en"]!!.get<YamlScalar>(text) != null)
            return true
        return false
    }

    fun exists(lang: String, text: String) = lang in dataBase && dataBase[lang]!!.get<YamlScalar>(text) != null

    fun existsLanguage(lang: String) = lang in dataBase

    fun getByLang(lang: String, text: String): String {
        if (lang !in dataBase || dataBase[lang]!!.get<YamlScalar>(text) == null) {
            if ("en" in dataBase && dataBase["en"]!!.get<YamlScalar>(text) != null)
                return dataBase["en"]!!.get<YamlScalar>(text)!!.content
            return text
        }
        return dataBase[lang]!!.get<YamlScalar>(text)!!.content
    }

    fun getByLang(lang: String, text: String, vararg objects: Any) = getByLang(lang, text).safeFormat(*objects)
    fun get(text: String, vararg objects: Any) = getByLang(language, text, *objects)

    fun getWithEn(text: String, vararg objects: Any): String {
        if (!exists(language, text) || language == "en" || get(text, *objects) == getByLang("en", text, *objects))
            return getByLang("en", text, *objects)
        return "${get(text, *objects)} (${getByLang("en", text, *objects)})"
    }

    fun getState(text: String, vararg objects: Any): MutableState<String> {
        if (!exists(text))
            return mutableStateOf(get(text, *objects))
        if (text !in states)
            states[text] = mutableStateOf(get(text, *objects))
        else
            states[text]!!.value = get(text, *objects)
        return states[text]!!
    }

    fun getStateWithEn(text: String, vararg objects: Any): MutableState<String> {
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