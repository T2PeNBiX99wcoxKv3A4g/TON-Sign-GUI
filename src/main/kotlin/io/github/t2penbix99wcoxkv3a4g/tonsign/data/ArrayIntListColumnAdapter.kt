package io.github.t2penbix99wcoxkv3a4g.tonsign.data

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.json.Json

object ArrayIntListColumnAdapter : ColumnAdapter<ArrayList<Int>, String> {
    override fun decode(databaseValue: String) = Json.decodeFromString<ArrayList<Int>>(databaseValue)

    override fun encode(value: ArrayList<Int>) = Json.encodeToString(value)
}