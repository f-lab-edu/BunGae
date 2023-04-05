package com.example.bungae.singleton

import android.annotation.SuppressLint
import com.example.bungae.data.ProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

object GetMyProfile {

    private var myProfile: ProfileData? = null

    suspend fun getMyProfile() : ProfileData? {
        FirebaseFirestore.getInstance().collection("Profile")
            .whereEqualTo("uid", FirebaseAuth.getInstance().currentUser?.uid)
            .get()
            .addOnSuccessListener { result ->
                val item = result.toObjects(ProfileData::class.java)
                if (item.size != 0) {
                    myProfile = item.get(0)
                }
            }.await()
        return myProfile
    }
}