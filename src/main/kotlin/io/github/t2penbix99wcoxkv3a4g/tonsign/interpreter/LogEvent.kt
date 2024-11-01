package io.github.t2penbix99wcoxkv3a4g.tonsign.interpreter

import java.time.ZonedDateTime

data class LogEvent(
    val time: ZonedDateTime,
    val level: LogLevel,
    val name: String?,
    val msg: String
)