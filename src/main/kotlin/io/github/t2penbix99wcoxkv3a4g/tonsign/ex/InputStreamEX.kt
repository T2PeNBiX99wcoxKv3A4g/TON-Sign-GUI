package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

import java.io.File
import java.io.InputStream

// https://pype.org/%E3%80%90kotlin%E3%80%91inputstream%E3%82%92%E3%83%95%E3%82%A1%E3%82%A4%E3%83%AB%E3%81%AB%E5%87%BA%E5%8A%9B/
fun InputStream.toFile(path: String) {
    File(path).outputStream().use {
        this.copyTo(it)
    }
}