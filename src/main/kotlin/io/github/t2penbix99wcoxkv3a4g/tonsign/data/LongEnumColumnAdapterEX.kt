package io.github.t2penbix99wcoxkv3a4g.tonsign.data

inline fun <reified T : Enum<T>> LongEnumColumnAdapter(): LongEnumColumnAdapter<T> = LongEnumColumnAdapter(enumValues())