package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.getTextOfRound
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.getTypeOfRound
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.jpToEn

fun main() {
    val list = mutableListOf<String>()

    RoundType.entries.forEach {
        val roundText = runCatching { it.getTextOfRound() }.getOrElse { "" }
        Utils.logger.debug { "${it.name} - $roundText" }
        list.add(roundText)
    }

    Utils.logger.debug { "----" }

    list.forEach {
        val en = it.jpToEn()
        Utils.logger.debug { "jp $it - $en" }
        Utils.logger.debug { "en $en - ${en.getTypeOfRound()}" }
    }
}