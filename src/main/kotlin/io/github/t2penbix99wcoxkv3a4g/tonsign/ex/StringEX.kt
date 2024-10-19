package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

fun String.toUTF8(): String {
    return this.toByteArray(Charsets.ISO_8859_1).decodeToString()
}