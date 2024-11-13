package io.github.t2penbix99wcoxkv3a4g.tonsign.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import io.github.t2penbix99wcoxkv3a4g.tonsign.event.Event
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.SaveManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.SecretsManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.CupcakeEXTheme
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tableSelection
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher

internal var logWatcher: LogWatcher? = null
internal val nextPrediction = mutableStateOf(GuessRoundType.NIL)
internal val onExitEvent = Event()

@Composable
@Preview
internal fun app() {
    CupcakeEXTheme {
        tableSelection()
    }
}

internal fun onExit() {
    onExitEvent()
    ConfigManager.save()
    SaveManager.save()
    SecretsManager.save()
}