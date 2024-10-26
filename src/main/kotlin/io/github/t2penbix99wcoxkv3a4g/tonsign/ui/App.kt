package io.github.t2penbix99wcoxkv3a4g.tonsign.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
import io.github.t2penbix99wcoxkv3a4g.tonsign.OSCSender
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.MaterialEXTheme
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.VRChatWatcher

var reader: LogWatcher? = null
var lastPrediction = GuessRoundType.NIL

/**
 * enum values that represent the screens in the app
 */
//enum class TonSignScreen(val title: String) {
//    Start(title = Utils.TITLE),
//    Test(title = "Test")
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun tonSignAppBar(
//    currentScreen: TonSignScreen,
//    canNavigateBack: Boolean,
//    navigateUp: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    TopAppBar(
//        title = { Text(currentScreen.title) },
//        colors = TopAppBarDefaults.mediumTopAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
//        ),
//        modifier = modifier,
//        navigationIcon = {
//            if (canNavigateBack) {
//                IconButton(onClick = navigateUp) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = "Back"
//                    )
//                }
//            }
//        }
//    )
//}
//
//@Composable
//fun TonSignApp(
//    viewModel: MainViewModel = viewModel { MainViewModel() },
//    navController: NavHostController = rememberNavController()
//) {
//    // Get current back stack entry
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    // Get the name of the current screen
//    val currentScreen = TonSignScreen.valueOf(
//        backStackEntry?.destination?.route ?: TonSignScreen.Start.name
//    )
//
//    Scaffold(
//        topBar = {
//            tonSignAppBar(
//                currentScreen = currentScreen,
//                canNavigateBack = navController.previousBackStackEntry != null,
//                navigateUp = { navController.navigateUp() }
//            )
//        }
//    ) { innerPadding ->
//        val uiState by viewModel.uiState.collectAsState()
//        NavHost(
//            navController = navController,
//            startDestination = TonSignScreen.Start.name,
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(innerPadding)
//        ) {
//            composable(route = TonSignScreen.Start.name) {
//                TODO("Screen")
//            }
//        }
//    }
//}

@Composable
@Preview
fun app(trayState: TrayState) {
    var lastPrediction by remember { mutableStateOf(lastPrediction) }
    var isWaitingVRChat by remember { mutableStateOf(VRChatWatcher.isWaitingVRChat) }

    MaterialEXTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(50.dp)
            ) {
                Button(
                    onClick = {
                        OSCSender.send(true)
                    }
                ) {
                    Text("Force send true")
                }
                Button(
                    onClick = {
                        OSCSender.send(false)
                    }
                ) {
                    Text("Force send false")
                }
                Button(
                    onClick = {
                        OSCSender.sendChat("Test 日本語")
                    }
                ) {
                    Text("Send Chat Test")
                }
                Box(
                    Modifier.fillMaxSize()
                        .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                ) {
                    if (isWaitingVRChat) {
                        textBox("VRChat is not running")
                    }

                    if (reader != null) {
                        if (reader!!.nextRoundGuess != GuessRoundType.NIL) {
                            val next = reader!!.nextRoundGuess

                            if (next != lastPrediction) {
                                lastPrediction = next

                                textBox("Next Round is $next")
                            }
                        }

                        textBox("Recent Rounds: ${reader!!.getRecentRoundsLog(ConfigManager.config.maxRecentRounds)}")
                    }
                }
            }
        }
    }
}

@Composable
fun textBox(text: String = "Item") {
    Box(
        Modifier.height(32.dp)
            .fillMaxWidth()
            .background(Color(0, 0, 0, 20))
            .padding(start = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        SelectionContainer {
            Text(text = text)
        }
    }
}