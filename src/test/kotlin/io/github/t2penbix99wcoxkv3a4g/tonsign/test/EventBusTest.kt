package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.Event
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventBus
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.Subscribe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("ControlFlowWithEmptyBody")
fun main() {
    Utils.logger.debug { "Test" }
    TestClass()

    val scope = CoroutineScope(Dispatchers.IO)

    scope.launch {
        delay(5000)
        repeat(5) {
            EventBus.publishSuspend(TestEvent(it))
            Utils.logger.debug { "Send Test $it" }
        }
        (5..10).forEach {
            EventBus.publishSuspend(TestEvent(it))
            Utils.logger.debug { "Send Test $it" }
        }
    }

    while (true) {
    }
}

class TestClass {
    init {
        EventBus.register(this)
    }
    
    private val testVal = "123"
    
    @Subscribe("Test")
    private fun test(event: TestEvent) {
        Utils.logger.debug { "Test Event $event ${event.test} $testVal" }
    }

    @Suppress("unused")
    fun test2() {
    }
}

class TestEvent(val test: Int) : Event
