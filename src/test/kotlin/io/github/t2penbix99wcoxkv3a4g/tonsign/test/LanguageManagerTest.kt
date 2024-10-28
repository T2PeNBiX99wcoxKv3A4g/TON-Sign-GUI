package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import com.charleskorn.kaml.Yaml
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.exception.FolderNotFoundException
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import kotlin.io.path.Path

fun main() {
    val dir = Path(Utils.currentWorkingDirectory, "language").toFile()
    if (!dir.exists()) {
        Logger.error(
            { "LanguageManagerTest" },
            FolderNotFoundException("'${dir.path}' is not exists!"),
            "Can't found 'language' folder! Language is not working anymore."
        )
        return
    }
    dir.listFiles { file, filename -> filename.endsWith(".yml") }.forEach {
        val data = Yaml.default.parseToYamlNode(it.readText())
        val langID = it.name.split('.')[0]

        Logger.debug("Test ${it.name}")
    }
}