package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    var language: String,
    var maxRecentRounds: Int,
    var autoSaveMinutes: Float,
    var roundNotify: Boolean,
    var roundNotifyOnlySpecial: Boolean,
    var playerJoinedNotify: Boolean,
    var playerLeftNotify: Boolean,
    var autoScrollToDown: Boolean,
    var onTop: Boolean,
    var automaticTurnOnSign: Boolean,
    var oscParamTonSign: String,
    var oscParamTonSignTabun: String,
    var oscParamTonSignOn: String
)