package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.yamlMap
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.safeFormat
import kotlin.io.path.Path

object LanguageManager {
    private const val DIR = "language"
    private val dataBase = mutableMapOf<String, YamlMap>()

    init {
        load()
    }

    fun load() {
        Path(Utils.currentWorkingDirectory, DIR).toFile().listFiles { file, filename ->
            return@listFiles filename.endsWith(".yml")
        }.forEach {
            val data = Yaml.default.parseToYamlNode(it.readText())
            val langID = it.name.split('.')[0]

            dataBase[langID] = data.yamlMap
        }
    }

    fun get(text: String): String {
        val language = ConfigManager.config.language

        if (language !in dataBase || dataBase[language]?.get<YamlScalar>(text) == null) {
            if ("en" in dataBase && dataBase["en"]!!.get<YamlScalar>(text) != null)
                return dataBase["en"]!!.get<YamlScalar>(text)!!.content
            return text
        }
        return dataBase[language]!!.get<YamlScalar>(text)!!.content
    }

    fun get(text: String, vararg objects: Any): String {
        return get(text).safeFormat(*objects)
    }
}