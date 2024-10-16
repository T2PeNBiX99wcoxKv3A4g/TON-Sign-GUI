package io.github.t2penbix99wcoxkv3a4g.tonsign

import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager

@Suppress("unused")
object Logger {
    fun debug(message: String, vararg objects: Any) =
        Utils.logger.debug { LanguageManager.get(message).format(*objects) }

    fun info(message: String, vararg objects: Any) =
        Utils.logger.info { LanguageManager.get(message).format(*objects) }

    fun warn(message: String, vararg objects: Any) =
        Utils.logger.warn { LanguageManager.get(message).format(*objects) }

    fun error(message: String, vararg objects: Any) =
        Utils.logger.error { LanguageManager.get(message).format(*objects) }

    fun debug(throwable: Throwable?, message: String, vararg objects: Any) =
        Utils.logger.debug(throwable) { LanguageManager.get(message).format(*objects) }

    fun info(throwable: Throwable?, message: String, vararg objects: Any) =
        Utils.logger.info(throwable) { LanguageManager.get(message).format(*objects) }

    fun warn(throwable: Throwable?, message: String, vararg objects: Any) =
        Utils.logger.warn(throwable) { LanguageManager.get(message).format(*objects) }

    fun error(throwable: Throwable?, message: String, vararg objects: Any) =
        Utils.logger.error(throwable) { LanguageManager.get(message).format(*objects) }
}