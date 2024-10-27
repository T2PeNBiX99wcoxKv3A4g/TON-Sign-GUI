package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import com.charleskorn.kaml.yamlMap
import com.charleskorn.kaml.yamlScalar
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.ConfigScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.safeFormat
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.io.path.Path
import kotlin.io.readText
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

object ConfigManager {
    private const val fileName = "config.yml"
    private val scope = ConfigScope()
    private val filePath = Path(Utils.currentWorkingDirectory, fileName)
    private val fileBakPath = Path(Utils.currentWorkingDirectory, "$fileName.bak")

    val Default = Config("en", 7, 30f, true)

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
        _config = runCatching { Yaml.default.decodeFromStream<Config>(file.inputStream()) }.getOrElse {
            val text =
                LanguageManager.getByLang("en", "exception.config_load_error").safeFormat(it.message ?: "Unknown")
            Utils.logger.error(it) { "[${this::class.simpleName!!}] $text" }

            return@getOrElse runCatching {
                val data = Yaml.default.parseToYamlNode(file.readText())
                val config = Default

                data.yamlMap.entries.filter { yaml ->
                    val prop = Config::class.memberProperties.find { return@find it.name == yaml.key.content }
                    return@filter prop != null
                }.forEach { yaml ->
                    val prop =
                        config::class.memberProperties.find { return@find it.name == yaml.key.content } as KMutableProperty1<Config, *>
                    val value = yaml.value.yamlScalar
                    val returnType = prop.returnType
                    val classifier = returnType.classifier!!
                    val kClass = classifier as KClass<*>

                    prop.setter.call(
                        config, when (kClass.qualifiedName) {
                            "kotlin.String" -> value.content
                            "kotlin.Int" -> value.toInt()
                            "kotlin.Float" -> value.toFloat()
                            "kotlin.Boolean" -> value.toBoolean()
                            "kotlin.Long" -> value.toLong()
                            "kotlin.Double" -> value.toDouble()
                            "kotlin.Byte" -> value.toByte()
                            "kotlin.Char" -> value.toChar()
                            "kotlin.Short" -> value.toShort()
                            else -> value.content
                        }
                    )
                    
                    Utils.logger.debug { "Yaml Value Type Qualified Name: ${kClass.qualifiedName}" }
                }

                return@getOrElse config
            }.getOrElse {
                Utils.logger.error(it) { "[${this::class.simpleName!!}] Config auto fix failed: ${it.message}" }
                renameFile()
                return@getOrElse Default
            }
        }
        save()
    }

    fun writeDefault() {
        file.writeText(Yaml.default.encodeToString(Config.serializer(), Default))
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