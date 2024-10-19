package io.github.t2penbix99wcoxkv3a4g.tonsign.logger

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.color.ANSIConstants
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase

class HighLightLogLevel : ForegroundCompositeConverterBase<ILoggingEvent>() {
    override fun getForegroundColorCode(event: ILoggingEvent): String {
        val level = event.level
        return when (level.toInt()) {
            Level.ERROR_INT -> ANSIConstants.RED_FG
            Level.WARN_INT -> ANSIConstants.YELLOW_FG
            Level.DEBUG_INT -> ANSIConstants.GREEN_FG
            else -> ANSIConstants.DEFAULT_FG
        }
    }
}