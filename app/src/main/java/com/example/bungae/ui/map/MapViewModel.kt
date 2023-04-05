package com.example.bungae.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bungae.data.ItemData
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val db: FirebaseFirestore,
) : ViewModel() {

    private val list: MutableList<ItemData> = mutableListOf()

    private val _itemList = MutableLiveData<MutableList<ItemData>>(mutableListOf())
    val itemList: LiveData<MutableList<ItemData>>
        get() = _itemList

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    fun getItemList() {
        db.collection("ItemInfo")
            .get()
            .addOnSuccessListener { results ->
                list.clear()
                for (result in results) {
                    val item = result.toObject(ItemData::class.java)
                    list.add(item)
                }

                list.sortByDescending { it.date }
                _itemList.value = list
            }
            .addOnFailureListener { e ->
                Log.e("Failed to get data", e.toString())
                _message.value = "데이터를 가져오는데 실패했습니다."
            }
    }
}