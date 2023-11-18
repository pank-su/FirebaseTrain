package su.pank.firebase_train.models

import com.google.firebase.firestore.DocumentReference

data class MenuItem(
    val name: String,
    val available: Boolean,
    val composition: String,
    val photoName: String,
    val price: Long,
    val reference: DocumentReference
){
    val imageUrl
        get() = "https://firebasestorage.googleapis.com/v0/b/fir-test-7a67f.appspot.com/o/${photoName}?alt=media"
}