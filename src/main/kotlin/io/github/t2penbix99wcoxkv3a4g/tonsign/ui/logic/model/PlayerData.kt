package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val name: String,
    var status: PlayerStatus,
    var deathMsg: String?
)