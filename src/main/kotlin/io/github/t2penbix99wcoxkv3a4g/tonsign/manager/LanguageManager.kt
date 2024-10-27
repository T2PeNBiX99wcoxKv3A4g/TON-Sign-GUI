package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.yamlMap
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
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
        get() = runCatching { return@runCatching ConfigManager.config.language }.getOrElse { return@getOrElse "en" }

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
            dir.listFiles { file, filename ->
                return@listFiles filename.endsWith(".yml")
            }.forEach {
                val data = Yaml.default.parseToYamlNode(it.readText())
                val langID = it.name.split('.')[0]

                dataBase[langID] = data.yamlMap
            }
        }.getOrElse {
            Logger.error({ this::class.simpleName!! }, it, "Initialization Failure: %s", it.message ?: "Unknown")
        }
    }

    fun exists(text: String): Boolean {
        if (language in dataBase && dataBase[language]?.get<YamlScalar>(text) != null)
            return true

        if ("en" in dataBase && dataBase["en"]!!.get<YamlScalar>(text) != null)
            return true
        return false
    }

    fun existsLang(lang: String) = lang in dataBase

    fun getByLang(lang: String, text: String): String {
        if (lang !in dataBase || dataBase[lang]?.get<YamlScalar>(text) == null) {
            if ("en" in dataBase && dataBase["en"]!!.get<YamlScalar>(text) != null)
                return dataBase["en"]!!.get<YamlScalar>(text)!!.content
            return text
        }
        return dataBase[lang]!!.get<YamlScalar>(text)!!.content
    }

    fun get(text: String): String {
        return getByLang(language, text)
    }

    fun get(text: String, vararg objects: Any): String {
        return get(text).safeFormat(*objects)
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

    fun getLang(): MutableState<String> {
        return langState
    }

    fun setLang(lang: String) {
        if (!existsLang(lang)) return
        runCatching { ConfigManager.config.language = lang }
        langState.value = lang
    }
}