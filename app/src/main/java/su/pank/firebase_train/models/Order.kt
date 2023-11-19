package su.pank.firebase_train.models

import com.google.firebase.firestore.DocumentReference
import kotlinx.datetime.LocalDateTime

data class Order(
    val dateTime: LocalDateTime,
    val location: String,
    val menuItems: List<MenuItem>,
    val uid: String,
    val reference: DocumentReference
)
