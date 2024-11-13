package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

import com.charleskorn.kaml.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.RoundData
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
                val prop = default!!::class.memberProperties.find { it.name == yaml.key.content }
                prop != null
            }.forEach { yaml ->
                val prop =
                    default!!::class.memberProperties.find { it.name == yaml.key.content } as KMutableProperty1<*, *>
                when (val value = yaml.value) {
                    is YamlScalar -> {
                        val value2 = value.yamlScalar
                        val returnType = prop.returnType
                        val classifier = returnType.classifier!!
                        val kClass = classifier as KClass<*>

                        runCatching {
                            prop.setter.call(
                                default, when (kClass.qualifiedName) {
                                    "kotlin.String" -> value2.content
                                    "kotlin.Int" -> value2.toInt()
                                    "kotlin.Float" -> value2.toFloat()
                                    "kotlin.Boolean" -> value2.toBoolean()
                                    "kotlin.Long" -> value2.toLong()
                                    "kotlin.Double" -> value2.toDouble()
                                    "kotlin.Byte" -> value2.toByte()
                                    "kotlin.Char" -> value2.toChar()
                                    "kotlin.Short" -> value2.toShort()
                                    else -> value2.content
                                }
                            )
                        }.getOrElse {
                            Utils.logger.debug(it) { "Yaml set failed: ${yaml.key.content}" }
                        }

                        Utils.logger.debug { "Yaml Value Type Qualified Name: ${yaml.key.content} | ${kClass.qualifiedName}" }
                    }

                    is YamlMap -> {
                        val value2 = value.yamlMap
                        val returnType = prop.returnType
                        val classifier = returnType.classifier!!
                        val kClass = classifier as KClass<*>

                        value2.entries

                        TODO("Not finish")
                    }

                    is YamlList -> {
                        val value2 = value.yamlList
                        val list = value2.items
                        val returnType = prop.returnType
                        val classifier = returnType.classifier!!
                        val kClass = classifier as KClass<*>

                        if (default is RoundData) {
                            list.forEach {
//                                it.key
                            }
                        } else {
                            list.forEach {
//                                it.yamlScalar
//                                it.yamlMap
                            }

                            TODO("Not finish")
                        }
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

private fun yamlScalarHandle(yamlScalar: YamlScalar) {

}

//fun Yaml.safeDecodeFromFile(
//    file: File,
//    default: RoundData,
//    onError: (Throwable) -> Unit,
//    onFailure: (Throwable) -> Unit
//): RoundData {
//    
//}