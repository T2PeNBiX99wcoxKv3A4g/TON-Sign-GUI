package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger

fun String.toUTF8(): String {
    return this.toByteArray(Charsets.ISO_8859_1).decodeToString()
}

fun String.safeFormat(vararg args: Any?): String {
    return runCatching { this.format(*args) }.getOrElse {
        Logger.error({ this::class.simpleName!! }, it, "exception.string_format_error", it.message!!)
        this
    }
}