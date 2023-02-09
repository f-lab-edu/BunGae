package com.example.bungae.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MypageViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Mypage Fragment"
    }
    val text: LiveData<String> = _text
}