package com.example.bungae.ui.account.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bungae.data.ProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val imageStorage: FirebaseStorage
) : ViewModel() {

    private var _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    private var _checkNickname = MutableLiveData<Boolean>()
    val checkNickname: LiveData<Boolean>
        get() = _checkNickname

    private var _checkFirestore = MutableLiveData<Boolean>(false)
    val checkFirestore: LiveData<Boolean>
        get() = _checkFirestore

    fun checkNickName(nickName: String) {
        viewModelScope.launch {Dispatchers.IO
            try {
                val dbResult = db.collection("Profile")
                    .whereEqualTo("nickname", nickName)
                    .get()
                    .await()
                _checkNickname.value = dbResult.documents.isEmpty()
            } catch (e: Exception) {
                _message.value = "서버와의 통신에 실패하였습니다."
            }
        }
    }

    fun createProfile(nickName: String, age: Int, gender: Boolean) {
        viewModelScope.launch {Dispatchers.IO
            try {
                if (nickName.isEmpty()) {
                    _message.value = "사용할 닉네임을 입력해주세요."
                } else {
                    val profileData = ProfileData(
                        uid = auth.currentUser!!.uid,
                        nickname = nickName,
                        age = age,
                        gender = gender,
                    )
                    db.collection("Profile").document(auth.currentUser!!.uid)
                        .set(profileData).await()
                    _message.value = "프로필 등록에 성공하였습니다."
                    _checkFirestore.value = true
                }
            } catch (e: Exception) {
                Log.e("프로필 등록 실패", "$e")
                _message.value = "프로필 등록에 실패하였습니다."
            }
        }
    }

    fun uploadImageToFirebase(uriInfo: Uri?) {
        viewModelScope.launch {Dispatchers.IO
            val imageRef =
                imageStorage.reference.child("profile/")
                    .child("image_${auth.currentUser!!.uid}.jpg")
            imageRef.putFile(uriInfo!!)
                .addOnSuccessListener {
                }
                .addOnFailureListener {
                }
        }
    }
}