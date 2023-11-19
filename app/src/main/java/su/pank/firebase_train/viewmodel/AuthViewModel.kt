package su.pank.firebase_train.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val _user = MutableStateFlow(Firebase.auth.currentUser)
    var user: StateFlow<FirebaseUser?> = _user
    var isReg by mutableStateOf(false)
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var secondPassword by mutableStateOf("")
    var invalidAuth by mutableStateOf(false)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            _user.emit(Firebase.auth.currentUser)

        }
    }

    fun loginOrReg() {
        if (!isReg)
            CoroutineScope(Dispatchers.IO).launch {
               try {
                   val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()
                   _user.emit(result.user)
               }catch (e: FirebaseAuthException) {
                   invalidAuth = true
               }
            }
        else{
            CoroutineScope(Dispatchers.IO).launch {
                val result = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
                _user.emit(result.user)
            }
        }
    }

}