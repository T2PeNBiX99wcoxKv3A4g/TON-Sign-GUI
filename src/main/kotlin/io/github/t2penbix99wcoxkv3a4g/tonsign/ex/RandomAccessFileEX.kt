package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

import java.io.RandomAccessFile

fun RandomAccessFile.readLineUTF8(): String {
    return this.readLine().toUTF8()
}