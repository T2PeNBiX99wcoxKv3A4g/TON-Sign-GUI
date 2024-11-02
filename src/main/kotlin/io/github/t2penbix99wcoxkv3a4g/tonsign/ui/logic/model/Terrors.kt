package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model

import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType

class Terrors(ids: ArrayList<Int>, val roundType: RoundType) {
    private companion object {
        private val specialRounds = listOf(
            RoundType.Bloodbath
        )
    }

    val terrors = listOf(
        Terror(1, ids[0], roundType), Terror(2, ids[1], roundType), Terror(3, ids[2], roundType)
    )

    val names: List<String>
        get() {
            val list = mutableListOf<String>()

            list.add(terrors[0].name)

            if (terrors[0].terrorId != Terror.UNKNOWN && roundType != RoundType.MysticMoon && roundType != RoundType.BloodMoon && roundType != RoundType.Twilight && roundType != RoundType.Solstice && roundType != RoundType.Run) {
                if (terrors[1].terrorId > 0 || roundType in specialRounds) list.add(terrors[1].name)

                if (terrors[2].terrorId > 0 || roundType in specialRounds) list.add(terrors[2].name)
            }

            return list.toList()
        }
}