package io.github.t2penbix99wcoxkv3a4g.tonsign

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Notification
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.LogicScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.EventBus
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.OnLogWatcherInitEvent
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.l10n
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.VRChatWatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var logWatcherIsStarted = false
private var runningTime = 0
private var needToWait = false

val delayToLoadingLog = mutableStateOf(false)
val logicHasError = mutableStateOf(false)
val logicError = mutableStateOf(Throwable())

internal fun startWatcher() {
    var delayToLoadingLog by delayToLoadingLog
    var logicHasError by logicHasError
    var logicError by logicError

    if (logWatcherIsStarted)
        return

    LogicScope.launch {
        runCatching { EventHandle }.getOrElse {
            Logger.error(
                it,
                { "exception.something_is_not_right" },
                it.localizedMessage,
                it.stackTraceToString()
            )
        }

        while (true) {
            runCatching {
                if (!VRChatWatcher.isVRChatRunning()) {
                    if (!needToWait)
                        needToWait = true
                    VRChatWatcher.checkVRChatLoop()
                }

                if (needToWait) {
                    Logger.info { "log.wait_until_join_game" }
                    delayToLoadingLog = true
                    delay(60000)
                }

                if (delayToLoadingLog)
                    delayToLoadingLog = false

                runningTime++
                logWatcher = LogWatcher.Default
                val logWatcher = logWatcher!!
                EventBus.publish(OnLogWatcherInitEvent(logWatcher))
                logWatcher.monitorRoundType()
                EventBus.unregister(logWatcher)
            }.getOrElse {
                logicError = it
                logicHasError = true
                Logger.error(it, { "exception.something_is_not_right" }, it.localizedMessage, it.stackTraceToString())
                
                val errorNotification = Notification(
                    "gui.notification.error_title".l10n(),
                    "gui.notification.error_message".l10n()
                )

                trayState.sendNotification(errorNotification)
                return@launch
            }
        }
    }

    logWatcherIsStarted = true
}