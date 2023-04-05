package com.example.bungae.ui.account.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
) : ViewModel() {

    private var _message = MutableLiveData<Boolean>()
    val message: LiveData<Boolean>
        get() = _message

    private var _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean>
        get() = _success

    fun signIn(email: String, passwd:String) {
        viewModelScope.launch {Dispatchers.IO
            if (email.isNotEmpty() && passwd.isNotEmpty()) {
                try {
                    val authResult = auth.signInWithEmailAndPassword(email, passwd).await()
                    _message.value = (authResult.user != null)
                } catch (e: Exception) {
                    _success.value = false
                }
            }
        }

    }
}