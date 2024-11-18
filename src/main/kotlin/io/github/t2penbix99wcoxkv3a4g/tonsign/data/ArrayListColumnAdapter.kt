package io.github.t2penbix99wcoxkv3a4g.tonsign.data

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Not working
class ArrayListColumnAdapter<T> : ColumnAdapter<ArrayList<T>, String> {
    override fun decode(databaseValue: String) = Json.decodeFromString<ArrayList<T>>(databaseValue)

    override fun encode(value: ArrayList<T>) = Json.encodeToString(value)
}