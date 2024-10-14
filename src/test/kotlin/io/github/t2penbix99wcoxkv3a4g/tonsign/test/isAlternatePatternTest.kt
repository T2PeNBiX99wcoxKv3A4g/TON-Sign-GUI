package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType

fun main() {
    val test = listOf(
        GuessRoundType.Classic,
        GuessRoundType.Classic,
        GuessRoundType.Special,
        GuessRoundType.Classic,
        GuessRoundType.Classic,
        GuessRoundType.Special,
        GuessRoundType.Classic,
        GuessRoundType.Classic
    )

    var test2 = test.takeLast(6).count { it == GuessRoundType.Special }

    Utils.logger.info { "Test a $test $test2" }
}