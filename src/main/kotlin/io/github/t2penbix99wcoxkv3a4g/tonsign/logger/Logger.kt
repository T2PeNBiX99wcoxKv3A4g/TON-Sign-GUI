package io.github.t2penbix99wcoxkv3a4g.tonsign.logger

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.safeFormat
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import kotlin.reflect.KClass

@Suppress("unused")
object Logger {
    fun debug(message: String, vararg objects: Any) =
        Utils.logger.debug { LanguageManager.get(message, *objects) }

    fun info(message: String, vararg objects: Any) =
        Utils.logger.info { LanguageManager.get(message, *objects) }

    fun warn(message: String, vararg objects: Any) =
        Utils.logger.warn { LanguageManager.get(message, *objects) }

    fun error(message: String, vararg objects: Any) =
        Utils.logger.error { LanguageManager.get(message, *objects) }

    fun debug(theClass: () -> String, message: String, vararg objects: Any) {
        val msg = LanguageManager.get(message)
        Utils.logger.debug { "[%s] $msg".safeFormat(theClass(), *objects) }
    }

    fun info(theClass: () -> String, message: String, vararg objects: Any) {
        val msg = LanguageManager.get(message)
        Utils.logger.info { "[%s] $msg".safeFormat(theClass(), *objects) }
    }

    fun warn(theClass: () -> String, message: String, vararg objects: Any) {
        val msg = LanguageManager.get(message)
        Utils.logger.warn { "[%s] $msg".safeFormat(theClass(), *objects) }
    }

    fun error(theClass: () -> String, message: String, vararg objects: Any) {
        val msg = LanguageManager.get(message)
        Utils.logger.error { "[%s] $msg".safeFormat(theClass(), *objects) }
    }

    fun debug(theClass: () -> String, throwable: Throwable?, message: String, vararg objects: Any) {
        val msg = LanguageManager.get(message)
        Utils.logger.debug(throwable) { "[%s] $msg".safeFormat(theClass(), *objects) }
    }

    fun info(theClass: () -> String, throwable: Throwable?, message: String, vararg objects: Any) {
        val msg = LanguageManager.get(message)
        Utils.logger.info(throwable) { "[%s] $msg".safeFormat(theClass(), *objects) }
    }

    fun warn(theClass: () -> String, throwable: Throwable?, message: String, vararg objects: Any) {
        val msg = LanguageManager.get(message)
        Utils.logger.warn(throwable) { "[%s] $msg".safeFormat(theClass(), *objects) }
    }

    fun error(theClass: () -> String, throwable: Throwable?, message: String, vararg objects: Any) {
        val msg = LanguageManager.get(message)
        Utils.logger.error(throwable) { "[%s] $msg".safeFormat(theClass(), *objects) }
    }

    fun debug(throwable: Throwable?, message: String, vararg objects: Any) =
        Utils.logger.debug(throwable) { LanguageManager.get(message, *objects) }

    fun info(throwable: Throwable?, message: String, vararg objects: Any) =
        Utils.logger.info(throwable) { LanguageManager.get(message, *objects) }

    fun warn(throwable: Throwable?, message: String, vararg objects: Any) =
        Utils.logger.warn(throwable) { LanguageManager.get(message, *objects) }

    fun error(throwable: Throwable?, message: String, vararg objects: Any) =
        Utils.logger.error(throwable) { LanguageManager.get(message, *objects) }
}