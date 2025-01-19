package io.github.t2penbix99wcoxkv3a4g.tonsign.data

import app.cash.sqldelight.ColumnAdapter
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundFlag
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundFlags
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.encode

object RoundFlagsColumnAdapter : ColumnAdapter<RoundFlags, Long> {
    override fun decode(databaseValue: Long): RoundFlags = RoundFlag.decode(databaseValue.toInt())

    override fun encode(value: RoundFlags): Long = value.encode().toLong()
}