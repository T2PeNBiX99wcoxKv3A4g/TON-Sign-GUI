package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.yamlMap
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import kotlin.io.path.Path

const val DIR = "Language"

fun main() {
    Path(Utils.currentWorkingDirectory, DIR).toFile().listFiles { file, filename ->
        return@listFiles filename.endsWith(".yml")
    }.forEach {
        val data = Yaml.default.parseToYamlNode(it.readText())
        val test = data.yamlMap.get<YamlScalar>("log.no_log_file")

        Utils.logger.info { "Data $test | ${test!!.content}" }
    }
}