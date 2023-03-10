package com.example.bungae.ui.message.message_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bungae.data.ChatModel
import com.example.bungae.singleton.FireBaseAuth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MessageViewModel() {

    private var list: MutableList<ChatModel> = mutableListOf()

    private var _chatList = MutableLiveData<MutableList<ChatModel>>()
    val chatList: LiveData<MutableList<ChatModel>>
        get() = _chatList

    fun getMyChatList() {
        FireBaseAuth.db.collection("ChatRoom")
            .whereEqualTo("users.${FireBaseAuth.auth.currentUser!!.uid}", true)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Listen failed.", e.toString())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    list.clear()
                    for (result in snapshot) {
                        val item = result.toObject(ChatModel::class.java)
                        list.add(item)
                    }
                    list.sortByDescending { it.comments.get("comment")!!.timestamp }
                    _chatList.value = list
                } else {
                    Log.e("Current data: null", "Current data: null")
                }
            }
    }
}