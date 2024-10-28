package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.SaveManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.RoundData

fun main() {
    val test = mutableListOf<RoundData>()

    test.add(RoundData(
        time = "",
        roundType = RoundType.Classic
    ))

    SaveManager.save = SaveManager.save.copy(test.toList())
}