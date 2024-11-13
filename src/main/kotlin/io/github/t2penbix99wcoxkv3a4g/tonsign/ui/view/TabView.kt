package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n

// https://github.com/olk90/compose-tableView/blob/main/src/main/kotlin/de/olk90/tableview/view/TabView.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun tableSelection() {
    val navController = rememberNavController()

    Column {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = { Text(text = Utils.TITLE) },
                    actions = {
//                        IconButton(onClick = {}) {
//                            Icon(Icons.Default.Menu, contentDescription = "Menu")
//                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar {
                    bottomNav(navController)
                }
            },
            content = {
                screenNavigation(navController, it)
            }
        )
    }
}

@Composable
fun screenNavigation(navController: NavHostController, padding: PaddingValues) {
    NavHost(navController = navController, startDestination = SelectionState.Main.gui.id) {
        SelectionState.entries.forEach { state ->
            composable(state.gui.id) {
                Row(
                    modifier = Modifier.padding(padding)
                ) {
                    Column(Modifier.fillMaxWidth(state.gui.maxWidth)) {
                        state.gui.view(navController, padding)
                    }
                    state.gui.detailView(navController, padding)
                }
            }
        }
    }
}

@Composable
fun bottomNav(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation(
        modifier = Modifier.height(80.dp),
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        SelectionState.entries.forEach {
            BottomNavigationItem(
                icon = {
                    it.gui.icon()
                },
                selected = currentDestination?.route == it.gui.id,
                onClick = {
                    navController.navigate(it.gui.id)
                },
                label = {
                    Text(it.gui.title.i18n())
                }
            )
        }
    }
}