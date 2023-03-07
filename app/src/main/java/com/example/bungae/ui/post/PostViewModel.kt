package com.example.bungae.ui.post

import android.location.Address
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bungae.data.ItemData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat

class PostViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val currentTime: Long = System.currentTimeMillis()
    private val dateFormat = SimpleDateFormat("yy-MM-dd_HH:mm:ss")

    private var _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean>
        get() = _success

    private var _blankCheck = MutableLiveData<Boolean>()
    val blankCheck: LiveData<Boolean>
        get() = _blankCheck

    private var _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String>
        get() = _imageUrl

    fun uploadImageToFirebase(uriInfo: Uri?) {
        val imageRef =
            FirebaseStorage.getInstance().reference.child("ItemInfo/")
                .child("image_${auth.currentUser!!.uid}_${dateFormat.format(currentTime)}.jpg")
        imageRef.putFile(uriInfo!!)
            .addOnSuccessListener {
                getItemUrl()
            }
            .addOnFailureListener {
            }
    }

    private fun getItemUrl() {
        val imgRef = FirebaseStorage.getInstance().reference.child(
            "ItemInfo/image_${auth.currentUser!!.uid}_${dateFormat.format(currentTime)}.jpg"
        )
        imgRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _imageUrl.value = task.result.toString()
            } else {

            }
        }
    }

    fun insertFireStorage(
        title: String,
        content: String,
        category: String,
        address: String,
        imageUrl: String
    ) {
        if (title.isBlank() || content.isBlank()) {
            _blankCheck.value = false
        } else {

            val itemData = ItemData(
                uid = auth.currentUser!!.uid,
                title = title,
                content = content,
                category = category,
                address = address,
                date = dateFormat.format(currentTime),
                imageUrl = imageUrl
            )

            db.collection("ItemInfo")
                .document("${auth.currentUser!!.uid}_${dateFormat.format(currentTime)}")
                .set(itemData)
                .addOnSuccessListener { result ->
                    Log.d("게시글 등록 성공", "$result")
                    _success.value = true
                }
                .addOnFailureListener { e ->
                    Log.e("게시글 등록 실패", "e")
                    _success.value = false
                }
        }
    }
}