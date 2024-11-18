package io.github.t2penbix99wcoxkv3a4g.tonsign.data

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Not working
class MutableListColumnAdapterInternal<T> : ColumnAdapter<MutableList<out T>, String> {
    override fun decode(databaseValue: String) = Json.decodeFromString<MutableList<out T>>(databaseValue)

    override fun encode(value: MutableList<out T>) = Json.encodeToString(value)
}