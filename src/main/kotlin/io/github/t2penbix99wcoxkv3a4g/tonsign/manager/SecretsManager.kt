package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.SecretsScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventBus
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.OnSecretsLoadedEvent
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.debug
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.path.Path

// TODO: sqlite3
internal object SecretsManager {
    private const val FILE_NAME = "secrets"
    private val filePath = Path(Utils.currentWorkingDirectory, FILE_NAME)
    private val fileBakPath = Path(Utils.currentWorkingDirectory, "${FILE_NAME}.bak")

    val Default = Secrets(
        username = null,
        password = null,
        serverCookieStore = hashMapOf(),
        clientCookieStore = hashMapOf()
    )

    private var _secrets: Secrets? = null
    val secrets: Secrets
        get() {
            if (_secrets == null)
                load()
            return _secrets!!
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

        SecretsScope.launch {
            autoSave()
        }
    }

    private fun load() {
        _secrets = runCatching { Json.decodeFromString<Secrets>(file.readText()) }.getOrElse {
            Utils.logger.error(it) { "[${this::class.simpleName}] Secrets load error: ${it.localizedMessage ?: "Unknown"}" }
            renameFile()
            Default
        }
        save()
        EventBus.publish(OnSecretsLoadedEvent(secrets))
    }

    fun save() {
        EventBus.publish(OnSecretsLoadedEvent(secrets))
        file.writeText(Json.encodeToString(Secrets.serializer(), secrets))
    }

    // https://gist.github.com/flymrc/31f0efe6cf927ff8d8e79d4c9ca9eb5c
    // https://www.baeldung.com/java-aes-encryption-decryption
    fun decrypt(algorithm: String, cipherText: String, key: SecretKeySpec, iv: IvParameterSpec): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText))
        return String(plainText)
    }

    fun encrypt(algorithm: String, inputText: String, key: SecretKeySpec, iv: IvParameterSpec): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val cipherText = cipher.doFinal(inputText.toByteArray())
        return Base64.getEncoder().encodeToString(cipherText)
    }

    private fun renameFile() {
        if (!file.exists()) return
        val fileBakFile = fileBakPath.toFile()

        if (fileBakFile.exists())
            fileBakFile.renameTo(
                Path(
                    Utils.currentWorkingDirectory,
                    "${FILE_NAME}.${Utils.timeNowForFile}.bak"
                ).toFile()
            )

        file.renameTo(fileBakFile)
    }

    private suspend fun autoSave() {
        while (true) {
            delay((ConfigManager.config.autoSaveMinutes * 60 * 1000).toLong())
            Logger.debug<SecretsManager> { "Auto save" }
        }
    }
}