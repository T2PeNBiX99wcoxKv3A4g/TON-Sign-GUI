package io.github.t2penbix99wcoxkv3a4g.tonsign

import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.LogicScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventArg
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.Save
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.SaveManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.VRChatWatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.set

private var logWatcherIsStarted = false
private var runningTime = 0
private val logicScope = LogicScope()
private var needToWait = false

val readerInitEvent = EventArg<LogWatcher>()

internal fun startWatcher() {
    if (logWatcherIsStarted)
        return

    logicScope.launch {
//        SaveManager.onLoadedSaveEvent += ::onLoadedSave Too slow
        SaveManager.onStartSaveEvent += ::onStartSave

        while (true) {
            runCatching {
                if (!VRChatWatcher.isVRChatRunning()) {
                    if (!needToWait)
                        needToWait = true
                    VRChatWatcher.checkVRChatLoop()
                }

                if (needToWait) {
                    Logger.info("log.wait_until_join_game")
                    delay(60000)
                }

                runningTime++
                logWatcher = LogWatcher.Default
                val logWatcher = logWatcher!!
                handle(logWatcher)
                readerInitEvent(logWatcher)
                logWatcher.monitorRoundType()
            }.getOrElse {
                Logger.error(it, "exception.something_is_not_right", it.message!!)
                needToWait = false
            }
        }
    }

    logWatcherIsStarted = true
}


internal fun onLoadedSave(save: Save) {
    val saveList = save.roundHistories
    if (saveList.isEmpty()) return

    saveList.forEach {
        val time = it.time
        roundDatas[time] = it
    }
}

private fun onStartSave(save: Save) {
    save.roundHistories = roundDatas.values.toList()
}