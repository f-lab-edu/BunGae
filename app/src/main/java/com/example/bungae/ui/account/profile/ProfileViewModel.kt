package com.example.bungae.ui.account.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bungae.data.ProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
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
        db.collection("Profile")
            .whereEqualTo("nickname", nickName)
            .get()
            .addOnSuccessListener { results ->
                _checkNickname.value = results.documents.isEmpty()
            }
            .addOnFailureListener {
                _message.value = "서버와의 통신에 실패하였습니다."
            }
    }

    fun createProfile(nickName: String, age: Int, gender: Boolean) {
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
                .set(profileData)
                .addOnSuccessListener {
                    _message.value = "프로필 등록에 성공하였습니다."
                    _checkFirestore.value = true
                }
                .addOnFailureListener { e ->
                    Log.e("프로필 등록 실패", "$e")
                    _message.value = "프로필 등록에 실패하였습니다."
                }
        }
    }

    fun uploadImageToFirebase(uriInfo: Uri?) {
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