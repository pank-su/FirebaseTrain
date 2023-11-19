package su.pank.firebase_train.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import su.pank.firebase_train.viewmodel.UserViewModel

@Composable
fun CartScreen(vm: UserViewModel, navController: NavController) {
    LaunchedEffect(key1 = vm.cart.size) {
        if (vm.cart.isEmpty()) {
            navController.popBackStack()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Box(modifier = Modifier.height(500.dp)) {
                Menu(menu = vm.cart.toHashSet().toList(), vm = vm, isManager = false)
            }
            Text(
                text = "Сумма: ${vm.cart.sumOf { it.price }} рублей",
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth().padding(end = 12.dp)
            )
        }


        Button(
            onClick = { navController.navigate("CheckOutScreen") }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(text = "Оформить")
        }
    }
}