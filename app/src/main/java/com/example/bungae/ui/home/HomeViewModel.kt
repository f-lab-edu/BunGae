package com.example.bungae.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bungae.data.ItemData
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel() : ViewModel() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val list: MutableList<ItemData> = mutableListOf()

    private val _itemList = MutableLiveData<MutableList<ItemData>>(mutableListOf())
    val itemList: LiveData<MutableList<ItemData>>
        get() = _itemList

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    fun getFireStorage() {
        db.collection("ItemInfo")
            .get()
            .addOnSuccessListener { results ->
                list.clear()
                for (result in results) {
                    val item = result.toObject(ItemData::class.java)
                    list.add(item)
                }

                // firebase order by로 수정할 것
                list.sortByDescending { it.date }
                _itemList.value = list
            }
            .addOnFailureListener { e ->
                Log.e("Failed to get data", e.toString())
                _message.value = "데이터를 가져오는데 실패했습니다."
            }
    }

}