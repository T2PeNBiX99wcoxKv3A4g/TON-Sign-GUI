package io.github.t2penbix99wcoxkv3a4g.tonsign.ui

//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.rememberDialogState
import io.github.t2penbix99wcoxkv3a4g.tonsign.OSCSender
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme.MaterialEXTheme
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.LogWatcher
import io.github.t2penbix99wcoxkv3a4g.tonsign.watcher.VRChatWatcher

var reader: LogWatcher? = null
val nextPrediction = mutableStateOf(GuessRoundType.NIL)

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
fun app(trayState: TrayState, needRestart: MutableState<Boolean>, needRefresh: MutableState<Boolean>) {
    var isWaitingVRChat by VRChatWatcher.isWaitingVRChat
    var needRefreshSet by needRefresh
    var nextPredictionSet by nextPrediction

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
                    Text(LanguageManager.getState("gui.button.force_send_true").value)
                }
                Button(
                    onClick = {
                        OSCSender.send(false)
                    }
                ) {
                    Text(LanguageManager.getState("gui.button.force_send_false").value)
                }
                Button(
                    onClick = {
                        LanguageManager.setLang("jp")
                        needRefreshSet = true
                    }
                ) {
                    Text("日本語")
                }
                Button(
                    onClick = {
                        LanguageManager.setLang("en")
                        needRefreshSet = true
                    }
                ) {
                    Text("English")
                }
                Button(
                    onClick = {
                        needRefreshSet = true
                    }
                ) {
                    Text(LanguageManager.getState("gui.button.refresh").value)
                }
                Box(
                    Modifier.fillMaxSize()
                        .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        if (isWaitingVRChat)
                            textBox(LanguageManager.getState("gui.text.vrchat_is_not_running").value)
                        
                        if (reader != null && reader!!.isInTON.value) {
                            val special = LanguageManager.getState("gui.text.round_special").value
                            val classic = LanguageManager.getState("gui.text.round_classic").value

                            if (nextPredictionSet == GuessRoundType.Special || nextPredictionSet == GuessRoundType.Classic)
                                textBox(
                                    LanguageManager.getState(
                                        "gui.text.next_prediction",
                                        if (nextPredictionSet == GuessRoundType.Special) special else classic
                                    ).value
                                )

                            val recentRounds = reader!!.getRecentRoundsLogState.value

                            if (!recentRounds.isBlank())
                                textBox(LanguageManager.getState("gui.text.recent_rounds", recentRounds).value)
                        }
                    }
                }
            }
        }
    }
}

private fun onExit() {
    ConfigManager.save()
}

val dialogSize = DpSize(350.dp, 280.dp)

@Composable
fun showConfirmExitWindow(isAskingToClose: MutableState<Boolean>, isOpen: MutableState<Boolean>) {
    var isAskingToCloseSet by isAskingToClose
    var isOpenSet by isOpen
    val state = rememberDialogState(position = Aligned(alignment = Alignment.Center), size = dialogSize)
    DialogWindow(
        onCloseRequest = { isAskingToCloseSet = false },
        title = LanguageManager.getState("gui.title.confirm_exit").value,
        state = state
    ) {
        MaterialEXTheme {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.padding(50.dp)
                ) {
                    Text(LanguageManager.getState("gui.text.confirm_exit").value)
                    Button(
                        onClick = {
                            onExit()
                            isOpenSet = false
                        }
                    ) {
                        Text(LanguageManager.getState("gui.button.yes").value)
                    }
                    Button(
                        onClick = { isAskingToCloseSet = false }
                    ) {
                        Text(LanguageManager.getState("gui.button.no").value)
                    }
                }
            }
        }
    }
}

@Composable
fun showNeedRestartWindows(needRestart: MutableState<Boolean>, isOpen: MutableState<Boolean>) {
    var needRestartSet by needRestart
    var isOpenSet by isOpen
    val state = rememberDialogState(position = Aligned(alignment = Alignment.Center), size = dialogSize)
    DialogWindow(
        onCloseRequest = { needRestartSet = false },
        title = LanguageManager.getState("gui.title.need_restart").value,
        state = state
    ) {
        MaterialEXTheme {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.padding(50.dp)
                ) {
                    Text(LanguageManager.getState("gui.text.need_restart").value)
                    Button(
                        onClick = {
                            onExit()
                            isOpenSet = false
                        }
                    ) {
                        Text(LanguageManager.getState("gui.button.yes").value)
                    }
                    Button(
                        onClick = { needRestartSet = false }
                    ) {
                        Text(LanguageManager.getState("gui.button.no").value)
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