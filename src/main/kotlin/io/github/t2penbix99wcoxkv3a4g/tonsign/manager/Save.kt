package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.RoundData
import kotlinx.serialization.Serializable

@Serializable
data class Save(
    var roundHistories: List<RoundData>
)