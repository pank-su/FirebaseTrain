package su.pank.firebase_train.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import su.pank.firebase_train.models.Order

sealed class CourierScreenState {
    data object Loading : CourierScreenState()
    data class Loaded(val orders: List<Order>) : CourierScreenState()
}

class CourierViewModel : ViewModel() {


    private val _state = MutableStateFlow<CourierScreenState>(CourierScreenState.Loading)
    val state = _state.asStateFlow()
    val db = Firebase.firestore


    init {
        reloadOrders()
    }

    private fun reloadOrders(){
        CoroutineScope(Dispatchers.IO).launch {
            _state.emit(
                CourierScreenState.Loaded(
                    db.collection("orders").get().await().documents.map {
                        it.toOrder()
                    })
            )
        }
    }

    fun removeOrder(order: Order) {
        db.collection("orders").document(order.reference.id).delete()
        reloadOrders()
    }
}

private suspend fun DocumentSnapshot.toOrder() = Order(
    this.getTimestamp("datetime")!!.let {
        Instant.fromEpochSeconds(it.seconds, it.nanoseconds).toLocalDateTime(
            TimeZone.currentSystemDefault()
        )
    },
    this.getString("location")!!,
    menuItems = (this.get("menuItems") as List<DocumentReference>).map {
        it.get().await().toMenuItem()
    },
    this.getString("uid")!!,
    this.reference
)
