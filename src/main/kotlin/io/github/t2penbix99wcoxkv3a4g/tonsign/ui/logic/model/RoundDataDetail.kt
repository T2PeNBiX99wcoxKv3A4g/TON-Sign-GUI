package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model

import kotlinx.serialization.Serializable

@Serializable
data class RoundDataDetail(
    val map: String,
    val mapId: Int,
    val time: Long,
    val playerTime: Long,
    val players: MutableList<PlayerData>,
    val terrors: ArrayList<Int>,
    val isDeath: Boolean,
    val isWon: WonOrLost
)