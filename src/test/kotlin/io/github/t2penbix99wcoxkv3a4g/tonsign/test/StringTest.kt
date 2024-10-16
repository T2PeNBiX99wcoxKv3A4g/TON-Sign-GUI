package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils

const val testString = "123456 round type is Some Round"

fun main() {
    val parts = testString.split("round type is")

    if (parts.size > 1) {
        val possibleRoundType = parts[1].substring(1, parts[1].length - 1)

        Utils.logger.info { "Test ($possibleRoundType)" }
    }
}