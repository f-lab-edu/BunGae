package com.example.bungae.ui.account.signup

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
class SignUpViewModel @Inject constructor(
    private val auth: FirebaseAuth,
) : ViewModel() {

    private var _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    fun checkPasswd(email: String, passwd1: String, passwd2: String) {
        if (passwd1 != passwd2) {
            _message.value = "비밀번호가 일치하지 않습니다."
        } else {
            createAccount(email, passwd1)
        }
    }

    private fun createAccount(email: String, passwd: String) {
        viewModelScope.launch {
            Dispatchers.IO
            if (email.isNotEmpty() && passwd.isNotEmpty()) {
                val authResult = auth.createUserWithEmailAndPassword(email, passwd).await()
                if (authResult.user != null) {
                    _message.value = "계정 생성을 완료했습니다."
                } else {
                    _message.value = "계정 생성에 실패하였습니다."
                }
            } else {
                _message.value = "빈칸을 전부 채워주세요!"
            }
        }

    }
}