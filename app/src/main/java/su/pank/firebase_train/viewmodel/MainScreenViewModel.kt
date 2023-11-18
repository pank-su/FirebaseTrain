package su.pank.firebase_train.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class Role{
    User, Courier, Manager
}

val stringToRole = mapOf(
    "user" to Role.User,
    "courier" to Role.Courier,
    "manager" to Role.Manager
)

sealed class MainScreenState(){
    data object Loading : MainScreenState()
    data class Loaded(val role: Role): MainScreenState()
}

class MainScreenViewModel: ViewModel() {
    val auth = Firebase.auth
    val db = Firebase.firestore
    private val _state = MutableStateFlow<MainScreenState>(MainScreenState.Loading)
    val state = _state.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser!!
            val roles = db.collection("roles")

            var currentUserData = db.collection("users").whereEqualTo("uid", user.uid).get().await()

            // Если у пользователя нет роли то выдаём
            if (currentUserData.isEmpty){
                val userRole = roles.whereEqualTo("name", "user").get().await().documents[0].reference
                db.collection("users").add(hashMapOf(
                    "uid" to user.uid,
                    "role" to userRole
                )).await()
                currentUserData = db.collection("users").whereEqualTo("uid", user.uid).get().await()
            }
            val role = currentUserData.documents[0].getDocumentReference("role")!!.get().await().get("name")
            _state.emit(MainScreenState.Loaded(stringToRole[role]!!))

        }

    }
}