package su.pank.firebase_train.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import su.pank.firebase_train.models.MenuItem
import su.pank.firebase_train.viewmodel.UserViewModel

@Composable
fun MenuScreen(vm: UserViewModel, navController: NavHostController) {
    val menu by vm.menu.collectAsState(initial = listOf())
    Box(modifier = Modifier.fillMaxSize()) {
        Menu(menu, vm)
        if (vm.cart.isNotEmpty())
            Button(
                onClick = { navController.navigate("Cart") }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text(text = "Перейти в корзину")
            }
    }
}

@Composable
fun Menu(
    menu: List<MenuItem>,
    vm: UserViewModel
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        items(menu) { menuItem: MenuItem ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = menuItem.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            12.dp,
                            Alignment.CenterVertically
                        ),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = menuItem.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = menuItem.composition,
                            style = MaterialTheme.typography.bodyMedium, maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(text = "${menuItem.price} рублей", )
                    }
                    if (!vm.cart.contains(menuItem))
                        IconButton(
                            onClick = { vm.cart.add(menuItem) },
                            Modifier.requiredSize(48.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, null)
                        }
                    else
                        Column(
                            Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(
                                2.dp,
                                Alignment.CenterVertically
                            )
                        ) {
                            FilledIconButton(onClick = { vm.cart.add(menuItem) }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                            Text(text = vm.cart.count { it == menuItem }.toString())
                            FilledIconButton(onClick = { vm.cart.remove(menuItem) }) {
                                Icon(Icons.Default.Remove, contentDescription = null)
                            }
                        }
                }
            }
        }
    }
}