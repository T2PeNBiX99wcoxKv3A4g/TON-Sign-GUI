@file:Suppress("unused")

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

fun String.lastPath(find: String): String {
    val index = this.indexOf(find)
    if (index < 0)
        return ""
    return this.substring(index + find.length)
}

fun String.lastPath(find: Char): String {
    val index = this.indexOf(find)
    if (index < 0)
        return ""
    return this.substring(index + 1)
}

fun String.firstPath(find: String): String {
    val index = this.indexOf(find)
    if (index < 0)
        return ""
    return this.substring(0, index)
}

fun String.firstPath(find: Char): String {
    val index = this.indexOf(find)
    if (index < 0)
        return ""
    return this.substring(0, index)
}

fun String.middlePath(first: String, last: String): String {
    return this.firstPath(last).lastPath(first)
}

fun String.middlePath(first: Char, last: Char): String {
    return this.firstPath(last).lastPath(first)
}

fun String.middlePath(first: Char, last: String): String {
    return this.firstPath(last).lastPath(first)
}

fun String.middlePath(first: String, last: Char): String {
    return this.firstPath(last).lastPath(first)
}