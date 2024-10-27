package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    var language: String,
    var maxRecentRounds: Int,
    var autoSaveMinutes: Float,
    var onlySpecial: Boolean
)