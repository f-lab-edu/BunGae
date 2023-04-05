package com.example.bungae.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bungae.R
import com.example.bungae.data.ItemData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

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

                list.sortByDescending { it.date }
                categoryCheck()
            }
            .addOnFailureListener { e ->
                Log.e("Failed to get data", e.toString())
                _message.value = "데이터를 가져오는데 실패했습니다."
            }
    }

    fun getMyPostList() {
        db.collection("ItemInfo")
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { results ->
                list.clear()
                for (result in results) {
                    val item = result.toObject(ItemData::class.java)
                    list.add(item)
                }

                list.sortByDescending { it.date }
                categoryCheck()
            }
            .addOnFailureListener { e ->
                Log.e("Failed to get data", e.toString())
                _message.value = "데이터를 가져오는데 실패했습니다."
            }
    }

    private fun categoryCheck() {
        for (i in list) {
            when (i.category) {
                "아무거나!" -> i.category = R.drawable.img_everything.toString()
                "카페 투어" -> i.category = R.drawable.img_coffee.toString()
                "운동" -> i.category = R.drawable.img_running.toString()
                "맛집 탐방" -> i.category = R.drawable.img_dinner.toString()
                "안주와 술" -> i.category =  R.drawable.img_beer.toString()
                "영화" ->i.category =  R.drawable.img_movie.toString()
            }
        }
        _itemList.value = list
    }

}