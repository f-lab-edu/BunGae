package com.example.bungae.singleton

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

object GetProfileImage {

    private var uri: Uri? = null

    suspend fun getProfileImage(uid: String) : Uri? {
        val imgRef = FirebaseStorage.getInstance().reference.child("profile/image_${uid}.jpg")
        imgRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                uri = task.result
            }
        }.await()
        return uri
    }
}