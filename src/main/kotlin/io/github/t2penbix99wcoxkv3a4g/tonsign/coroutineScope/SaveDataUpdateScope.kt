package io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

internal object SaveDataUpdateScope : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job() + Executors.newSingleThreadExecutor().asCoroutineDispatcher() + CoroutineName("SaveDataUpdate")
}