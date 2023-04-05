package com.example.bungae.ui.mypage.mypage

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bungae.data.ProfileData
import com.example.bungae.singleton.GetProfileImage
import com.example.bungae.singleton.GetProfileImage.getProfileImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
        viewModelScope.launch {
            Dispatchers.IO
            try {
                val dbResult = db.collection("Profile")
                    .whereEqualTo("uid", auth.currentUser!!.uid)
                    .get()
                    .await()
                val item = dbResult.toObjects(ProfileData::class.java)
                if (item.size != 0) {
                    _listProfileData.value = item[0]
                    getProfileImage()
                }
            } catch (e: Exception) {

            }
        }
    }

    fun getProfileImage() {
        viewModelScope.launch {
            Dispatchers.IO
            try {
                val imgRef =
                    imageStorage.reference.child("profile/image_${auth.currentUser!!.uid}.jpg")
                val imageResult = imgRef.downloadUrl.await()
                _task.value = imageResult
            } catch (e: Exception) {
                _loadImageSuccess.value = false
            }
        }
    }


    fun updateImageToFirebase(uriInfo: Uri) {
        viewModelScope.launch { Dispatchers.IO
            try {
                val fileName = "image_${auth.currentUser!!.uid}.jpg"
                val imageRef = imageStorage.reference.child("profile/").child(fileName)
                imageRef.putFile(uriInfo).await()
            } catch (e: Exception) {
            }
        }
    }

    fun checkNickName(nickName: String) {
        viewModelScope.launch {Dispatchers.IO
            val dbResult = db.collection("Profile")
                .whereEqualTo("nickname", nickName)
                .get()
                .await()
            _checkNickname.value = dbResult.documents.isEmpty()
        }
    }

    fun updateNickName(nickName: String) {
        viewModelScope.launch { Dispatchers.IO
                db.collection("Profile")
                    .document(auth.currentUser!!.uid)
                    .update("nickname", nickName)
                    .await()
            }
    }
}