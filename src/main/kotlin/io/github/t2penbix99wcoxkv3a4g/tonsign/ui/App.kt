package io.github.t2penbix99wcoxkv3a4g.tonsign.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.TrayState
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.SaveManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.MaterialEXTheme
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tableSelection
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher

internal var logWatcher: LogWatcher? = null
internal val nextPrediction = mutableStateOf(GuessRoundType.NIL)

@Composable
@Preview
internal fun app(trayState: TrayState, needRestart: MutableState<Boolean>, needRefresh: MutableState<Boolean>) {
    MaterialEXTheme {
        tableSelection(trayState, needRestart, needRefresh)
    }
}

internal fun onExit() {
    ConfigManager.save()
    SaveManager.save()
}