package io.github.t2penbix99wcoxkv3a4g.tonsign.data

import app.cash.sqldelight.ColumnAdapter
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object PlayerDataListColumnAdapter : ColumnAdapter<MutableList<PlayerData>, String> {
    override fun decode(databaseValue: String) = Json.decodeFromString<MutableList<PlayerData>>(databaseValue)

    override fun encode(value: MutableList<PlayerData>) = Json.encodeToString(value)
}