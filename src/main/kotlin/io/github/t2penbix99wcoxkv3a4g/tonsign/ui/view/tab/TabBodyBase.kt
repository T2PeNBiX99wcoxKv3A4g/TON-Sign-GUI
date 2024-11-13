package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab.tray.TrayItem

abstract class TabBodyBase {
    abstract val title: String
    abstract val id: String

    open val isOnTop: MutableState<Boolean>
        get() = mutableStateOf(false)

    open val maxWidth: Float
        get() = 1f

    open val trays: List<TrayItem>
        get() = listOf<TrayItem>()
    
    open val enabled: Boolean
        get() = true

    @Composable
    abstract fun icon()

    @Composable
    abstract fun view(navController: NavHostController, padding: PaddingValues)

    @Composable
    open fun detailView(navController: NavHostController, padding: PaddingValues) {
    }

    @Composable
    open fun topMenu() {
    }
}