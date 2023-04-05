package com.example.bungae.ui.detail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bungae.data.ItemData
import com.example.bungae.data.ProfileData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class  DetailViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val imageStorage: FirebaseStorage
) : ViewModel() {

    private var _profileDataList = MutableLiveData<ProfileData>()
    val profileDataList: LiveData<ProfileData>
        get() = _profileDataList

    private var _porfileImage = MutableLiveData<Uri>()
    val porfileImage: LiveData<Uri>
        get() = _porfileImage

    private var _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean>
        get() = _deleteResult

    fun getProfileData(user: String) {
        db.collection("Profile")
            .whereEqualTo("uid", user)
            .get()
            .addOnSuccessListener { result ->
                val item = result.toObjects(ProfileData::class.java)
                _profileDataList.value = item.get(0)
            }
            .addOnFailureListener { e ->
                Log.e("Failed to get ProfileData", e.toString())
            }
    }

    fun getProfileImage(user: String) {
        val imgRef = imageStorage.reference.child("profile/image_${user}.jpg")
        imgRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _porfileImage.value = task.result
            } else {

            }
        }
    }

    fun deleteItem(item: ItemData) {
        db.collection("ItemInfo")
            .document("${item.uid}_${item.date}")
            .delete()
            .addOnSuccessListener {
                _deleteResult.value = true
            }
            .addOnFailureListener {
                _deleteResult.value = false
            }
    }
}