package com.example.bungae.singleton

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

object GetProfileImage {

    suspend fun getProfileImage(uid: String): Uri? {
        return try {
            val imgRef = FirebaseStorage.getInstance().reference.child("profile/image_${uid}.jpg")
            val downloadUrl = imgRef.downloadUrl.await()
            downloadUrl
        } catch (e: Exception) {
            null
        }
    }
}