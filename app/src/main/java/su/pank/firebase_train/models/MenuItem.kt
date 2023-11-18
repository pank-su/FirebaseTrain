package su.pank.firebase_train.models

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

var storage = Firebase.storage

data class MenuItem(
    val name: String,
    val available: Boolean,
    val composition: String,
    val photoName: String,
    val price: Long
){
    val imageUrl
        get() = "https://firebasestorage.googleapis.com/v0/b/fir-test-7a67f.appspot.com/o/${photoName}?alt=media"
}