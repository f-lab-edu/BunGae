package com.example.bungae.ui.post

import android.location.Address
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bungae.data.ItemData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) : ViewModel() {

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

    private var _coordinates = MutableLiveData<Address?>()
    val coordinates: LiveData<Address?>
        get() = _coordinates

    private var _item = MutableLiveData<ItemData>()
    val item: LiveData<ItemData>
        get() = _item

    private var _map = MutableLiveData<HashMap<String, String>>()
    val map: LiveData<HashMap<String, String>>
        get() = _map

    fun sendItem(item: ItemData) {
        _item.value = item
    }

    fun uploadImageToFirebase(uriInfo: Uri?) {
        val imageRef =
            FirebaseStorage.getInstance().reference.child("ItemInfo/")
                .child("image_${auth.currentUser!!.uid}_${dateFormat.format(currentTime)}.jpg")
        imageRef.putFile(uriInfo!!)
            .addOnSuccessListener {
                getImageUrl()
            }
            .addOnFailureListener {
            }
    }

    private fun getImageUrl() {
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

    fun updateImageToFirebase(uriInfo: Uri, date: String) {
        val fileName = "image_${auth.currentUser!!.uid}_${date}.jpg"
        val imageRef = FirebaseStorage.getInstance().reference.child("ItemInfo/").child(fileName)
        imageRef.putFile(uriInfo).addOnSuccessListener {
            getUpdateItemUrl(date)
        }.addOnFailureListener {

        }
    }

    private fun getUpdateItemUrl(date: String) {
        val imgRef = FirebaseStorage.getInstance().reference.child(
            "ItemInfo/image_${auth.currentUser!!.uid}_${date}.jpg"
        )
        imgRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _imageUrl.value = task.result.toString()
            } else {

            }
        }
    }

    fun updateItem(
        uriInfo: String,
        title: String,
        content: String,
        category: String,
        address: String,
        date: String
    ) {
        if (title.isBlank() || content.isBlank()) {
            _blankCheck.value = false
        } else {

            val map: HashMap<String, String> = hashMapOf(
                "imageUrl" to uriInfo,
                "title" to title,
                "content" to content,
                "category" to category,
                "address" to address
            )
            db.collection("ItemInfo")
                .document("${auth.currentUser!!.uid}_${date}")
                .update(map as Map<String, Any>)
                .addOnSuccessListener {
                    _success.value = true
                    _map.value = map
                }
                .addOnFailureListener {
                    _success.value = false
                }
        }
    }

    fun sendCoordinates(address: Address?) {
        _coordinates.value = address
    }
}