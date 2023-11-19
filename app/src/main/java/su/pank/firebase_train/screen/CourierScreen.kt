package su.pank.firebase_train.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import su.pank.firebase_train.ui.theme.FirebaseTrainTheme
import su.pank.firebase_train.viewmodel.CourierScreenState
import su.pank.firebase_train.viewmodel.CourierViewModel

@Composable
fun CourierScreen() {
    var selectedTab by remember {
        mutableIntStateOf(0)
    }
    val vm: CourierViewModel = viewModel()
    val state by vm.state.collectAsState()

    when (state) {
        CourierScreenState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }

        is CourierScreenState.Loaded -> {
            Column {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.height(64.dp)
                    ) {
                        Text(text = "Все заказы")
                    }
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.height(64.dp)
                    ) {
                        Text(text = "Заказы на сегодня")
                    }
                }
                val orders by remember {
                    derivedStateOf {
                        if (selectedTab == 0)
                            (state as CourierScreenState.Loaded).orders
                        else
                            (state as CourierScreenState.Loaded).orders.filter {
                                it.dateTime.date == Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                            }

                    }
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        if (orders.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "Заказов нет", modifier = Modifier.align(
                                        Alignment.Center
                                    )
                                )
                            }
                        }
                    }
                    items(orders.size) { index ->
                        val order = orders[index]
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(
                                            CircleShape
                                        )
                                        .background(if (index % 2 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)

                                ) {
                                    Text(
                                        text = (index + 1).toString(),
                                        modifier = Modifier.align(Alignment.Center),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (index % 2 == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(
                                    Modifier.fillMaxWidth(0.8f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = order.location)
                                    Text(text = order.dateTime.toString())
                                    Text(
                                        text = order.menuItems.toHashSet()
                                            .joinToString(separator = ", ") { menuItem ->
                                                "${menuItem.name} x ${order.menuItems.count { it == menuItem }}"
                                            }
                                    )
                                }
                                IconButton(onClick = { vm.removeOrder(order) }) {
                                    Icon(Icons.Default.Done, contentDescription = null)
                                }
                            }
                        }
                    }
                }

            }
        }
    }

}

@Preview
@Composable
private fun PreviewCourierScreen() {
    FirebaseTrainTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CourierScreen()
        }
    }
}