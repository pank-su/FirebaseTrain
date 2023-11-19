package su.pank.firebase_train.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.github.jan.supabase.compose.auth.ui.AuthForm
import io.github.jan.supabase.compose.auth.ui.LocalAuthState
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField
import io.github.jan.supabase.compose.auth.ui.password.PasswordRule
import su.pank.firebase_train.viewmodel.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController) {
    val viewModel: AuthViewModel = viewModel()
    val currentUser by viewModel.user.collectAsState()
    LaunchedEffect(currentUser) {
        println(currentUser)
        if (currentUser != null) {
            navController.navigate("MainScreen")
        }
    }

    AuthForm {
        val state = LocalAuthState.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedEmailField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = {
                    Text(
                        text = "Почта"
                    )
                })
            OutlinedPasswordField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it }, label = {
                    Text(
                        text = "Пароль"
                    )
                }, rules = listOf(PasswordRule.minLength(6))
            )
            if (viewModel.isReg) {
                OutlinedPasswordField(
                    value = viewModel.secondPassword,
                    onValueChange = { viewModel.secondPassword = it },
                    label = {
                        Text(
                            text = "Повторите пароль"
                        )
                    },
                    rules = listOf(
                        PasswordRule.minLength(6),
                        PasswordRule("Пароли должны совпадать") { it == viewModel.password }
                    )
                )
            }
            Button(onClick = {
                             viewModel.loginOrReg()
            }, enabled = state.validForm) {
                Text(text = if (!viewModel.isReg) "Войти" else "Регистрация")
            }
            Button(onClick = { viewModel.isReg = !viewModel.isReg }) {
                Text(text = if (!viewModel.isReg) "Перейти к регистрации" else "Перейти к авторизации")
            }
        }

    }
    if (viewModel.invalidAuth){
        AlertDialog(onDismissRequest = { viewModel.invalidAuth = false }, confirmButton = { Button(
            onClick = { viewModel.invalidAuth = false }) {
            Text(text = "OK")
        } }, title = { Text(text = "Ошибка")}, text = {Text(text = "Неверный логин или пароль")})
    }

}