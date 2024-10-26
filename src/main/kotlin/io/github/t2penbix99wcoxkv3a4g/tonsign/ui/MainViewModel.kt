package io.github.t2penbix99wcoxkv3a4g.tonsign.ui

//import androidx.lifecycle.ViewModel
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.MainUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

//class MainViewModel : ViewModel() {
//    private val _uiState = MutableStateFlow(MainUiState())
//    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
//
//    fun test() {
//        _uiState.update {
//            it.copy(test = "Test")
//        }
//    }
//
//    fun reset() {
//        _uiState.value = MainUiState()
//    }
//}