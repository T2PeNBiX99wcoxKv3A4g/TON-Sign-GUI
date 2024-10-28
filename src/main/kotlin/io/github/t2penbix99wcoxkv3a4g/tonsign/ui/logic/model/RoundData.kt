package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model

import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.TableHeader
import kotlinx.serialization.Serializable

@Serializable
data class RoundData(
    @TableHeader("gui.tab.table_header.log_data.time", 0)
    val time: String,
    @TableHeader("Round Type", 1)
    val roundType: RoundType
)