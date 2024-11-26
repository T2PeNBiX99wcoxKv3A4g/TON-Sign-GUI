@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER", "unused")

package io.github.t2penbix99wcoxkv3a4g.tonsign.logger

import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.toStringSafe
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager

inline fun <reified T : Any> Logger.debug(noinline message: () -> Any?) {
    val msg = LanguageManager.get(message.toStringSafe())
    this.debug({ "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.info(noinline message: () -> Any?) {
    val msg = LanguageManager.get(message.toStringSafe())
    this.info({ "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.warn(noinline message: () -> Any?) {
    val msg = LanguageManager.get(message.toStringSafe())
    this.warn({ "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.error(noinline message: () -> Any?) {
    val msg = LanguageManager.get(message.toStringSafe())
    this.error({ "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.debug(noinline message: () -> Any?, vararg objects: Any?) {
    val msg = LanguageManager.get(message.toStringSafe())
    this.debug({ "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.info(noinline message: () -> Any?, vararg objects: Any?) {
    val msg = LanguageManager.get(message.toStringSafe())
    this.info({ "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.warn(noinline message: () -> Any?, vararg objects: Any?) {
    val msg = LanguageManager.get(message.toStringSafe())
    this.warn({ "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.error(noinline message: () -> Any?, vararg objects: Any?) {
    val msg = LanguageManager.get(message.toStringSafe())
    this.error({ "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.debug(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?
) {
    val msg = LanguageManager.get(message.toStringSafe(throwable))
    this.debug(throwable, { "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.info(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?
) {
    val msg = LanguageManager.get(message.toStringSafe(throwable))
    this.info(throwable, { "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.warn(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?
) {
    val msg = LanguageManager.get(message.toStringSafe(throwable))
    this.warn(throwable, { "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.error(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?
) {
    val msg = LanguageManager.get(message.toStringSafe(throwable))
    this.error(throwable, { "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.debug(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?,
    vararg objects: Any?
) {
    val msg = LanguageManager.get(message.toStringSafe(throwable))
    this.debug(throwable, { "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.info(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?,
    vararg objects: Any?
) {
    val msg = LanguageManager.get(message.toStringSafe(throwable))
    this.info(throwable, { "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.warn(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?,
    vararg objects: Any?
) {
    val msg = LanguageManager.get(message.toStringSafe(throwable))
    this.warn(throwable, { "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.error(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?,
    vararg objects: Any?
) {
    val msg = LanguageManager.get(message.toStringSafe(throwable))
    this.error(throwable, { "[%s] $msg" }, T::class.simpleName, *objects)
}