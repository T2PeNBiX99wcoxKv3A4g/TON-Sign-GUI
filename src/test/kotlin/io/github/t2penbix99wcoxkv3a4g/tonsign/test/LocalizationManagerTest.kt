package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import com.charleskorn.kaml.Yaml
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.asResourceFile

fun main() {
    val dir = "/localization".asResourceFile()

    Utils.logger.debug { "File: $dir" }

    if (dir == null) return

    Utils.logger.debug { dir.exists() }
    dir.listFiles { file, filename -> filename.endsWith(".yml") }?.forEach {
        val data = Yaml.default.parseToYamlNode(it.readText())
        val langID = it.name.split('.')[0]

        Utils.logger.debug { "Test ${it.name}" }
    }

//    val dir = Path(Utils.currentWorkingDirectory, "language").toFile()
//    if (!dir.exists()) {
//        Logger.error(
//            { "LanguageManagerTest" },
//            FolderNotFoundException("'${dir.path}' is not exists!"),
//            "Can't found 'language' folder! Language is not working anymore."
//        )
//        return
//    }
//    dir.listFiles { file, filename -> filename.endsWith(".yml") }.forEach {
//        val data = Yaml.default.parseToYamlNode(it.readText())
//        val langID = it.name.split('.')[0]
//
//        Logger.debug("Test ${it.name}")
//    }
}