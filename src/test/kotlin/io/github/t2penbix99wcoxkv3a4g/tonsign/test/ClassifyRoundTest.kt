package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.classifyRound

val roundLog = listOf<GuessRoundType>()
val testRound = RoundType.Twilight

fun isAlternatePattern(): Boolean {
    if (roundLog.size < 3) {
        return if (roundLog.size > 1)
            roundLog[1] == GuessRoundType.Special
        else
            false
    }

    if (roundLog.size < 4) {
        val last = roundLog.takeLast(3)
        return last[0] != last[1] && last[1] != last[2]
    }

    val last = roundLog.takeLast(4)
    return last[0] == last[2] && last[1] == last[3]
}

fun main() {
    var classification = testRound.classifyRound()

    if (classification == GuessRoundType.Exempt) {
        if (roundLog.size >= 2) {
            val last = roundLog.takeLast(2)
            when {
                last[0] == GuessRoundType.Classic && last[1] == GuessRoundType.Classic -> classification =
                    GuessRoundType.Special

                last[0] == GuessRoundType.Classic && last[1] == GuessRoundType.Special -> classification =
                    GuessRoundType.Classic

                last[0] == GuessRoundType.Special && last[1] == GuessRoundType.Classic -> classification =
                    if (isAlternatePattern()) GuessRoundType.Special else GuessRoundType.Classic
            }
        }
        else
            classification = GuessRoundType.Classic
    }

    Utils.logger.debug { "classification: $classification" }
}