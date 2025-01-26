@file:Suppress("unused")

package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.l10n
import java.io.File
import java.net.URL
import java.security.MessageDigest

fun String.toUTF8() = toByteArray(Charsets.ISO_8859_1).decodeToString()

fun String.safeFormat(vararg args: Any?): String {
    if (Utils.logger.isDebugEnabled())
        return format(*args)
    return runCatching { format(*args) }.getOrElse {
        Utils.logger.error(it) { "exception.string_format_error".l10n(it.localizedMessage) }
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

fun String.middlePath(first: String, last: String) = firstPath(last).lastPath(first)
fun String.middlePath(first: Char, last: Char) = firstPath(last).lastPath(first)
fun String.middlePath(first: Char, last: String) = firstPath(last).lastPath(first)
fun String.middlePath(first: String, last: Char) = firstPath(last).lastPath(first)

fun String.asResource(work: (String) -> Unit) {
    val content = asResourceUrl()?.readText() ?: return
    work(content)
}

fun String.asResourceUrl(): URL? = Utils.resourceUrl(this)
fun String.asResourceFile(): File? = Utils.resourceFile(this)

fun String.md5() = hashString(this, "MD5")
fun String.sha256() = hashString(this, "SHA-256")

// https://gist.github.com/lovubuntu/164b6b9021f5ba54cefc67f60f7a1a25
private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold(StringBuilder()) { sb, it -> sb.append("%02x".format(it)) }.toString()
}