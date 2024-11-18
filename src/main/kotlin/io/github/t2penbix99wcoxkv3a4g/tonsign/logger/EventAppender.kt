package io.github.t2penbix99wcoxkv3a4g.tonsign.logger

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventBus
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.OnLogAppendEvent

class EventAppender : AppenderBase<ILoggingEvent>() {
    override fun append(eventObject: ILoggingEvent) {
        EventBus.publish(OnLogAppendEvent(eventObject))
    }
}