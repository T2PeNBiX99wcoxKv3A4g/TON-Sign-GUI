package io.github.t2penbix99wcoxkv3a4g.tonsign.logger

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventArg

class EventAppender : AppenderBase<ILoggingEvent>() {
    companion object {
        val onLogAppendEvent = EventArg<ILoggingEvent>()
    }

    override fun append(eventObject: ILoggingEvent) {
        onLogAppendEvent(eventObject)
    }
}