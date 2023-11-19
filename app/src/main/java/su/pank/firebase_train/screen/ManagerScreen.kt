package su.pank.firebase_train.screen

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toFile
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import su.pank.firebase_train.ui.theme.FirebaseTrainTheme
import su.pank.firebase_train.viewmodel.UserViewModel
import java.io.File
import java.io.FileInputStream

data class Destination(val name: String, val icon: ImageVector, val content: @Composable () -> Unit)

@Composable
fun ManagerScreen() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    val menu by userViewModel.menu.collectAsState(initial = listOf())
    var addToMenuDialog by remember {
        mutableStateOf(false)
    }
    if (addToMenuDialog) {
        Dialog(onDismissRequest = { addToMenuDialog = false }) {
            var name by remember {
                mutableStateOf("")
            }
            var composition by remember {
                mutableStateOf("")
            }
            var price by remember {
                mutableStateOf("")
            }
            var photoUri by remember {
                mutableStateOf(Uri.EMPTY)
            }
            val activityResult =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        photoUri = uri
                        Log.d("PhotoPicker", "Selected URI: $uri")
                    } else {
                        Log.d("PhotoPicker", "No media selected")
                    }
                }
            Surface {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = "Название") })
                    TextField(
                        value = composition,
                        onValueChange = { composition = it },
                        label = { Text(text = "Состав") })

                    TextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text(text = "Цена") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Button(onClick = {
                        activityResult.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Text(text = if (photoUri == Uri.EMPTY) "Выбрать фото" else "Фото выбрано")
                        if (photoUri != Uri.EMPTY) {
                            AsyncImage(photoUri, contentDescription = null)
                        }
                    }

                    val context = LocalContext.current
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val fileName = context.contentResolver.query(
                                    photoUri,
                                    null,
                                    null,
                                    null,
                                    null
                                )!!.let {
                                    val index = it.getColumnIndex(
                                        OpenableColumns.DISPLAY_NAME
                                    )
                                    it.moveToFirst()
                                    val name = it.getString(index)
                                    it.close()
                                    name
                                }
                                val storage = Firebase.storage
                                val storageRef = storage.reference
                                val newImageReference = storageRef.child(fileName)
                                val stream = context.contentResolver.openInputStream(photoUri)!!

                                newImageReference.putStream(stream).await()
                                Firebase.firestore.collection("menu").add(
                                    hashMapOf(
                                        "name" to name,
                                        "available" to true,
                                        "composition" to composition,
                                        "photo_name" to fileName,
                                        "price" to price.toInt()
                                    )
                                )
                                addToMenuDialog = false
                            }

                        },
                        enabled = photoUri != Uri.EMPTY && name.isNotBlank() && composition.isNotBlank() && price.isDigitsOnly()
                    ) {
                        Text(text = "Создать")
                    }
                }
            }

        }
    }
    val destinations = listOf(Destination("Меню", Icons.Outlined.MenuBook) {
        Box(modifier = Modifier.fillMaxSize()) {
            Menu(menu = menu, userViewModel, isManager = true)
            Button(
                onClick = { addToMenuDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(text = "Добавить новый элемент меню")
            }
        }
    },
        Destination("Заказы", Icons.Outlined.List) {
            CourierScreen()
        })
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == destination.name } == true,
                        onClick = {
                            navController.navigate(destination.name) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, null) },
                        label = { Text(text = destination.name) })
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = destinations.first().name,
            modifier = Modifier.padding(it)
        ) {
            destinations.forEach { destination ->
                composable(destination.name) {
                    destination.content()
                }
            }
        }
    }
}

@Preview
@Composable
private fun ManagerScreenPreview() {
    FirebaseTrainTheme {
        Surface {
            ManagerScreen()
        }
    }
}