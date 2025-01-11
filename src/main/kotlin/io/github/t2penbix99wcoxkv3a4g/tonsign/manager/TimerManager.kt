package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.TimerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Timer class
object TimerManager {
    private var mainTime = 0L
    private val times = mutableMapOf<String, Long>()

    val time: Long
        get() = mainTime

    init {
        startMainTime()
    }

    private fun startMainTime() {
        TimerScope.launch {
            while (true) {
                mainTime++
                delay(1)
            }
        }
    }

    fun set(name: String) {
        times[name] = mainTime
    }

    fun get(name: String): Long {
        if (name !in times)
            return 0
        return mainTime - times[name]!!
    }
}