package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.VRChatWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun main() {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    Utils.logger.info { "VRChatWatcher Test" }
    
    scope.launch {
        VRChatWatcher.checkVRChatLoop()
    }
}