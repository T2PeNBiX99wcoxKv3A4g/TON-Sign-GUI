package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model

import androidx.compose.runtime.snapshots.SnapshotStateList
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType

// TODO: Unbound and Double Trouble, 8 pages terror
class Terrors {
    val ids: ArrayList<Int>
    val roundType: RoundType
    val terrors: List<Terror>

    constructor(ids: ArrayList<Int>, roundType: RoundType) {
        this.ids = ids
        this.roundType = roundType

        terrors = listOf(
            Terror(1, ids[0], roundType), Terror(2, ids[1], roundType), Terror(3, ids[2], roundType)
        )
    }

    constructor(ids: SnapshotStateList<Int>, roundType: RoundType) {
        this.ids = arrayListOf(ids[0], ids[1], ids[2])
        this.roundType = roundType

        terrors = listOf(
            Terror(1, ids[0], roundType), Terror(2, ids[1], roundType), Terror(3, ids[2], roundType)
        )
    }

    private companion object {
        private val specialRounds = listOf(
            RoundType.Bloodbath
        )
    }

    val names: List<String>
        get() {
            val list = mutableListOf<String>()

            list.add(terrors[0].name)
            
            if (roundType == RoundType.EightPages && terrors[0].isHideTerror())
                return list.toList()

            if (terrors[0].terrorId != Terror.UNKNOWN && roundType != RoundType.MysticMoon && roundType != RoundType.BloodMoon && roundType != RoundType.Twilight && roundType != RoundType.Solstice && roundType != RoundType.Run) {
                if (terrors[1].terrorId > 0 || roundType in specialRounds) list.add(terrors[1].name)

                if (terrors[2].terrorId > 0 || roundType in specialRounds) list.add(terrors[2].name)
            }

            return list.toList()
        }
}