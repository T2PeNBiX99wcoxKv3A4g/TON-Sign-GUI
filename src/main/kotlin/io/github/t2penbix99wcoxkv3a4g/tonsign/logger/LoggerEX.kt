@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER", "unused")

package io.github.t2penbix99wcoxkv3a4g.tonsign.logger

import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.toStringSafe
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LocalizationManager
import kotlinx.coroutines.Job

inline fun <reified T : Any> Logger.debug(noinline message: () -> Any?): Job {
    val msg = LocalizationManager.get(message.toStringSafe())
    return this.debug({ "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.info(noinline message: () -> Any?): Job {
    val msg = LocalizationManager.get(message.toStringSafe())
    return this.info({ "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.warn(noinline message: () -> Any?): Job {
    val msg = LocalizationManager.get(message.toStringSafe())
    return this.warn({ "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.error(noinline message: () -> Any?): Job {
    val msg = LocalizationManager.get(message.toStringSafe())
    return this.error({ "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.debug(noinline message: () -> Any?, vararg objects: Any?): Job {
    val msg = LocalizationManager.get(message.toStringSafe())
    return this.debug({ "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.info(noinline message: () -> Any?, vararg objects: Any?): Job {
    val msg = LocalizationManager.get(message.toStringSafe())
    return this.info({ "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.warn(noinline message: () -> Any?, vararg objects: Any?): Job {
    val msg = LocalizationManager.get(message.toStringSafe())
    return this.warn({ "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.error(noinline message: () -> Any?, vararg objects: Any?): Job {
    val msg = LocalizationManager.get(message.toStringSafe())
    return this.error({ "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.debug(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?
): Job {
    val msg = LocalizationManager.get(message.toStringSafe(throwable))
    return this.debug(throwable, { "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.info(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?
): Job {
    val msg = LocalizationManager.get(message.toStringSafe(throwable))
    return this.info(throwable, { "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.warn(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?
): Job {
    val msg = LocalizationManager.get(message.toStringSafe(throwable))
    return this.warn(throwable, { "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.error(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?
): Job {
    val msg = LocalizationManager.get(message.toStringSafe(throwable))
    return this.error(throwable, { "[%s] $msg" }, T::class.simpleName)
}

inline fun <reified T : Any> Logger.debug(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?,
    vararg objects: Any?
): Job {
    val msg = LocalizationManager.get(message.toStringSafe(throwable))
    return this.debug(throwable, { "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.info(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?,
    vararg objects: Any?
): Job {
    val msg = LocalizationManager.get(message.toStringSafe(throwable))
    return this.info(throwable, { "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.warn(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?,
    vararg objects: Any?
): Job {
    val msg = LocalizationManager.get(message.toStringSafe(throwable))
    return this.warn(throwable, { "[%s] $msg" }, T::class.simpleName, *objects)
}

inline fun <reified T : Any> Logger.error(
    throwable: Throwable?,
    noinline message: (Throwable?) -> Any?,
    vararg objects: Any?
): Job {
    val msg = LocalizationManager.get(message.toStringSafe(throwable))
    return this.error(throwable, { "[%s] $msg" }, T::class.simpleName, *objects)
}