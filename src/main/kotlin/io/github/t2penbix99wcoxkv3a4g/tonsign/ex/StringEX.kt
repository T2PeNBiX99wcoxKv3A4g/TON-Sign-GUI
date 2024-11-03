@file:Suppress("unused")

package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import java.io.File
import java.security.MessageDigest

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

fun String.asResource(work: (String) -> Unit) {
    val content = object {}.javaClass.getResource(this)?.readText()
    if (content == null) return
    work(content)
}

fun String.asResourceUrl() = object {}.javaClass.getResource(this)
fun String.asResourceFile(): File? {
    val url = asResourceUrl()
    if (url == null)
        return null
    return File(url.file)
}

fun String.md5() = hashString(this, "MD5")
fun String.sha256() = hashString(this, "SHA-256")

// https://gist.github.com/lovubuntu/164b6b9021f5ba54cefc67f60f7a1a25
private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold(StringBuilder()) { sb, it -> sb.append("%02x".format(it)) }.toString()
}