package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNull
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.YamlTaggedNode
import com.charleskorn.kaml.decodeFromStream
import com.charleskorn.kaml.yamlList
import com.charleskorn.kaml.yamlMap
import com.charleskorn.kaml.yamlScalar
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

inline fun <reified T> Yaml.safeDecodeFromFile(
    file: File,
    default: T,
    onError: (Throwable) -> Unit,
    onFailure: (Throwable) -> Unit
): T {
    return runCatching { decodeFromStream<T>(file.inputStream()) }.getOrElse {
        onError(it)

        runCatching {
            val data = parseToYamlNode(file.readText())

            data.yamlMap.entries.filter { yaml ->
                val prop = default::class.memberProperties.find { it.name == yaml.key.content }
                prop != null
            }.forEach { yaml ->
                val prop =
                    default::class.memberProperties.find { it.name == yaml.key.content } as KMutableProperty1<*, *>
                val value = yaml.value

                when (value) {
                    is YamlScalar -> {
                        val value = value.yamlScalar
                        val returnType = prop.returnType
                        val classifier = returnType.classifier!!
                        val kClass = classifier as KClass<*>

                        runCatching {
                            prop.setter.call(
                                default, when (kClass.qualifiedName) {
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
                        }.getOrElse {
                            Utils.logger.debug(it) { "Yaml set failed: ${yaml.key.content}" }
                        }

                        Utils.logger.debug { "Yaml Value Type Qualified Name: ${yaml.key.content} | ${kClass.qualifiedName}" }
                    }

                    is YamlList -> {
                        val value = value.yamlList
                        val list = value.items
                        val returnType = prop.returnType
                        val classifier = returnType.classifier!!
                        val kClass = classifier as KClass<*>

                        list.forEach {

                        }

                        TODO("Not finish")
                    }

                    is YamlMap -> {
                        val value = value.yamlMap
                        val returnType = prop.returnType
                        val classifier = returnType.classifier!!
                        val kClass = classifier as KClass<*>

                        value.entries

                        TODO("Not finish")
                    }

                    is YamlNull -> TODO("Not finish")
                    is YamlTaggedNode -> TODO("Not finish")
                }
            }

            default
        }.getOrElse {
            onFailure(it)
            default
        }
    }
}