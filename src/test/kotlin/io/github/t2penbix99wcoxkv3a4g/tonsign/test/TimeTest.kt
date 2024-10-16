package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")
    println("Current Date and Time is: ${current.format(formatter)}")
}