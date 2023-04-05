package com.example.bungae.ui.detail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bungae.data.ItemData
import com.example.bungae.data.ProfileData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val imageStorage: FirebaseStorage
) : ViewModel() {

    private var _profileDataList = MutableLiveData<ProfileData>()
    val profileDataList: LiveData<ProfileData>
        get() = _profileDataList

    private var _profileImage = MutableLiveData<Uri>()
    val profileImage: LiveData<Uri>
        get() = _profileImage

    private var _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean>
        get() = _deleteResult

    fun getProfileData(user: String) {
        viewModelScope.launch {
            Dispatchers.IO
            try {
                val dbResult = db.collection("Profile")
                    .whereEqualTo("uid", user)
                    .get()
                    .await()
                val item = dbResult.toObjects(ProfileData::class.java)
                _profileDataList.value = item[0]
            } catch (e: Exception) {
                Log.e("Failed to get ProfileData", e.toString())
            }
        }
    }

    fun getProfileImage(user: String) {
        viewModelScope.launch {
            Dispatchers.IO
            val imgRef = imageStorage.reference.child("profile/image_${user}.jpg")
            val downloadUrl = imgRef.downloadUrl.await()
            _profileImage.value = downloadUrl
        }
    }

    fun deleteItem(item: ItemData) {
        viewModelScope.launch {
            Dispatchers.IO
            try {
                db.collection("ItemInfo")
                    .document("${item.uid}_${item.date}")
                    .delete()
                    .await()
                _deleteResult.value = true

            } catch (e: Exception) {
                _deleteResult.value = false
            }
        }
    }
}