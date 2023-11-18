package su.pank.firebase_train.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import su.pank.firebase_train.viewmodel.MainScreenState
import su.pank.firebase_train.viewmodel.MainScreenViewModel
import su.pank.firebase_train.viewmodel.Role

@Composable
fun MainScreen() {
    val vm: MainScreenViewModel = viewModel()
    val state by vm.state.collectAsState()
    when (state) {
        MainScreenState.Loading -> Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }
        is MainScreenState.Loaded->{
            when ((state as MainScreenState.Loaded).role){
                Role.User -> UserScreen()
                Role.Manager -> ManagerScreen()
                Role.Courier -> CourierScreen()
            }
        }
    }
}