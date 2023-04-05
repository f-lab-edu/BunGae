package com.example.bungae.ui.mypage.mypage

import android.net.Uri
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
class MyPageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val imageStorage: FirebaseStorage
) : ViewModel() {

    private var _task = MutableLiveData<Uri>()
    val task: LiveData<Uri>
        get() = _task

    private var _loadImageSuccess = MutableLiveData<Boolean>(true)
    val loadImageSuccess: LiveData<Boolean>
        get() = _loadImageSuccess

    private var _listProfileData = MutableLiveData<ProfileData>()
    val listProfileData: LiveData<ProfileData>
        get() = _listProfileData

    private var _checkNickname = MutableLiveData<Boolean>()
    val checkNickname: LiveData<Boolean>
        get() = _checkNickname

    fun getNickname() {
        db.collection("Profile")
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { result ->
                val item = result.toObjects(ProfileData::class.java)
                if (item.size != 0) {
                    _listProfileData.value = item.get(0)
                    getProfileImage()
                }
            }
    }

    fun getProfileImage() {
        val imgRef = imageStorage.reference.child("profile/image_${auth.currentUser!!.uid}.jpg")
        imgRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _task.value = task.result
            } else {
                _loadImageSuccess.value = false
            }
        }
    }

    fun updateImageToFirebase(uriInfo: Uri) {
        val fileName = "image_${auth.currentUser!!.uid}.jpg"
        val imageRef = imageStorage.reference.child("profile/").child(fileName)
        imageRef.putFile(uriInfo).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun checkNickName(nickName: String) {
        db.collection("Profile")
            .whereEqualTo("nickname", nickName)
            .get()
            .addOnSuccessListener { results ->
                _checkNickname.value = results.documents.isEmpty()
            }
            .addOnFailureListener {

            }
    }

    fun updateNickName(nickName: String) {
        db.collection("Profile")
            .document(auth.currentUser!!.uid)
            .update("nickname", nickName)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }
}