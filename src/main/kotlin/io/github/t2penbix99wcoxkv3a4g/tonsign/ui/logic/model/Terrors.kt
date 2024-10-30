package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model

import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType

class Terrors(ids: ArrayList<Int>, val roundType: RoundType) {
    val terrors = listOf(
        Terror(ids[0], roundType),
        Terror(ids[1], roundType),
        Terror(ids[2], roundType)
    )

    val names: List<String>
        get() {
            val list = mutableListOf<String>()

            list.add(terrors[0].name)

            if (terrors[0].id != Terror.UNKNOWN) {
                if (terrors[1].id > 0)
                    list.add(terrors[1].name)

                if (terrors[2].id > 0)
                    list.add(terrors[2].name)
            }

            return list.toList()
        }
}