package io.github.t2penbix99wcoxkv3a4g.tonsign.data

import app.cash.sqldelight.ColumnAdapter

class LongEnumColumnAdapter<T : Enum<T>>(private val enumValues: Array<out T>) : ColumnAdapter<T, Long> {
    override fun decode(databaseValue: Long) = enumValues[databaseValue.toInt()]

    override fun encode(value: T) = value.ordinal.toLong()
}

inline fun <reified T : Enum<T>> LongEnumColumnAdapter(): LongEnumColumnAdapter<T> {
    return LongEnumColumnAdapter(enumValues())
}