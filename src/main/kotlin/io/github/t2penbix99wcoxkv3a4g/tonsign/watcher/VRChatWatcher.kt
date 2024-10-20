package io.github.t2penbix99wcoxkv3a4g.tonsign.watcher

import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import kotlinx.coroutines.delay

object VRChatWatcher {
    private var _isWaitingVRChat = false

    val isWaitingVRChat: Boolean
        get() = _isWaitingVRChat

    fun isVRChatRunning(): Boolean {
        val allProcesses = ProcessHandle.allProcesses()
        var isRunning = false

        allProcesses.filter { it.isAlive }.forEach {
            val command = it.info().command()
            val name = command.get()

            if (name.contains("VRChat.exe")) {
                isRunning = true
                return@forEach
            }
        }

        return isRunning
    }

    suspend fun checkVRChatLoop() {
        var count = 0
        _isWaitingVRChat = true

        while (true) {
            if (count < 1)
                Logger.info("log.waiting_vrchat")

            val check = isVRChatRunning()
            Logger.debug({ this::class.simpleName!! }, "Check: $check")

            if (check) {
                Logger.info("log.vrchat_found")
                _isWaitingVRChat = false
                break
            }

            count++
            delay(2000)
        }
    }
}