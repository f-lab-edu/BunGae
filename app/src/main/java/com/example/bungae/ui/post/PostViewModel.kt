package com.example.bungae.ui.post

import android.location.Address
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bungae.data.ItemData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
        viewModelScope.launch {
            Dispatchers.IO
            val imageRef =
                FirebaseStorage.getInstance().reference.child("ItemInfo/")
                    .child("image_${auth.currentUser!!.uid}_${dateFormat.format(currentTime)}.jpg")
            imageRef.putFile(uriInfo!!)
                .await()
            getImageUrl()
        }
    }

    private fun getImageUrl() {
        viewModelScope.launch {
            Dispatchers.IO
            val imgRef = FirebaseStorage.getInstance().reference.child(
                "ItemInfo/image_${auth.currentUser!!.uid}_${dateFormat.format(currentTime)}.jpg"
            )
            val imageResult = imgRef.downloadUrl.await()
            _imageUrl.value = imageResult.toString()

        }
    }

    fun insertFireStorage(
        title: String,
        content: String,
        category: String,
        address: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            Dispatchers.IO
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
                try {
                    db.collection("ItemInfo")
                        .document("${auth.currentUser!!.uid}_${dateFormat.format(currentTime)}")
                        .set(itemData)
                        .await()
                    _success.value = true
                } catch (e: Exception) {
                    _success.value = false
                }
            }
        }
    }

    fun updateImageToFirebase(uriInfo: Uri, date: String) {
        viewModelScope.launch {
            Dispatchers.IO
            val fileName = "image_${auth.currentUser!!.uid}_${date}.jpg"
            val imageRef =
                FirebaseStorage.getInstance().reference.child("ItemInfo/").child(fileName)
            imageRef.putFile(uriInfo).await()
            getUpdateItemUrl(date)
        }
    }

    private fun getUpdateItemUrl(date: String) {
        viewModelScope.launch {
            Dispatchers.IO
            val imgRef = FirebaseStorage.getInstance().reference.child(
                "ItemInfo/image_${auth.currentUser!!.uid}_${date}.jpg"
            )
            val imageResult = imgRef.downloadUrl.await()
            _imageUrl.value = imageResult.toString()
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
        viewModelScope.launch {
            Dispatchers.IO
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
                try {
                    val dbResult =
                        db.collection("ItemInfo")
                            .document("${auth.currentUser!!.uid}_${date}")
                            .update(map as Map<String, Any>)
                            .await()
                    _success.value = true
                    _map.value = map
                } catch (e: Exception) {
                    _success.value = false
                }
            }
        }
    }

    fun sendCoordinates(address: Address?) {
        _coordinates.value = address
    }
}