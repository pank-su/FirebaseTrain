package su.pank.firebase_train.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import su.pank.firebase_train.models.MenuItem
import kotlin.time.Duration.Companion.minutes

class UserViewModel : ViewModel() {
    val db = Firebase.firestore

    val cart = mutableStateListOf<MenuItem>()

    var location by mutableStateOf("")
    var date by mutableStateOf(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    )
    var time by mutableStateOf(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
    )



    val menu = flow {
        while (true) {
            emit(db.collection("menu").get().await().documents.map {
                it.toMenuItem()
            })
            kotlinx.coroutines.delay(10.minutes)
        }
    }
}

private fun DocumentSnapshot.toMenuItem(): MenuItem = MenuItem(
    this.getString("name")!!,
    this.getBoolean("available")!!,
    this.getString("composition")!!,
    this.getString("photo_name")!!,
    this.get("price")!! as Long,
)
