package io.github.t2penbix99wcoxkv3a4g.tonsign.logger

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.LoggerScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.toStringSafe
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import kotlinx.coroutines.launch

@Suppress("unused")
object Logger {
    fun debug(message: () -> Any?) = LoggerScope.launch {
        Utils.logger.debug { LanguageManager.get(message.toStringSafe()) }
    }

    fun info(message: () -> Any?) = LoggerScope.launch {
        Utils.logger.info { LanguageManager.get(message.toStringSafe()) }
    }

    fun warn(message: () -> Any?) = LoggerScope.launch {
        Utils.logger.warn { LanguageManager.get(message.toStringSafe()) }
    }

    fun error(message: () -> Any?) = LoggerScope.launch {
        Utils.logger.error { LanguageManager.get(message.toStringSafe()) }
    }

    fun debug(message: () -> Any?, vararg objects: Any?) = LoggerScope.launch {
        Utils.logger.debug { LanguageManager.get(message.toStringSafe(), *objects) }
    }

    fun info(message: () -> Any?, vararg objects: Any?) = LoggerScope.launch {
        Utils.logger.info { LanguageManager.get(message.toStringSafe(), *objects) }
    }

    fun warn(message: () -> Any?, vararg objects: Any?) = LoggerScope.launch {
        Utils.logger.warn { LanguageManager.get(message.toStringSafe(), *objects) }
    }

    fun error(message: () -> Any?, vararg objects: Any?) = LoggerScope.launch {
        Utils.logger.error { LanguageManager.get(message.toStringSafe(), *objects) }
    }

    fun debug(throwable: Throwable?, message: (Throwable?) -> Any?) = LoggerScope.launch {
        Utils.logger.debug(throwable) { LanguageManager.get(message.toStringSafe(throwable)) }
    }

    fun info(throwable: Throwable?, message: (Throwable?) -> Any?) = LoggerScope.launch {
        Utils.logger.info(throwable) { LanguageManager.get(message.toStringSafe(throwable)) }
    }

    fun warn(throwable: Throwable?, message: (Throwable?) -> Any?) = LoggerScope.launch {
        Utils.logger.warn(throwable) { LanguageManager.get(message.toStringSafe(throwable)) }
    }

    fun error(throwable: Throwable?, message: (Throwable?) -> Any?) = LoggerScope.launch {
        Utils.logger.error(throwable) { LanguageManager.get(message.toStringSafe(throwable)) }
    }

    fun debug(throwable: Throwable?, message: (Throwable?) -> Any?, vararg objects: Any?) = LoggerScope.launch {
        Utils.logger.debug(throwable) {
            LanguageManager.get(
                message.toStringSafe(throwable),
                *objects
            )
        }
    }

    fun info(throwable: Throwable?, message: (Throwable?) -> Any?, vararg objects: Any?) = LoggerScope.launch {
        Utils.logger.info(throwable) { LanguageManager.get(message.toStringSafe(throwable), *objects) }
    }

    fun warn(throwable: Throwable?, message: (Throwable?) -> Any?, vararg objects: Any?) = LoggerScope.launch {
        Utils.logger.warn(throwable) { LanguageManager.get(message.toStringSafe(throwable), *objects) }
    }

    fun error(throwable: Throwable?, message: (Throwable?) -> Any?, vararg objects: Any?) = LoggerScope.launch {
        Utils.logger.error(throwable) { LanguageManager.get(message.toStringSafe(throwable), *objects) }
    }
}