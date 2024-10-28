package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.TrayState
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundDatas
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.RoundData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tableView

class TabBodyRoundDatas : TabBodyBase() {
    override val title = { "Round Datas" }

    @Composable
    override fun view(
        trayState: TrayState,
        needRestart: MutableState<Boolean>,
        needRefresh: MutableState<Boolean>
    ) {
        val roundData: MutableState<RoundData?> = remember { mutableStateOf(null) }

        tableView(roundData, roundDatas, onRowSelection = {
            roundData.value = it
        })
    }
}