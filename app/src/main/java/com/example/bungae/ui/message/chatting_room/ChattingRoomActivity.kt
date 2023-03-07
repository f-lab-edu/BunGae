package com.example.bungae.ui.message.chatting_room

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bungae.data.ChatInfoData
import com.example.bungae.data.ProfileData
import com.example.bungae.databinding.ActivityChattingRoomDetailBinding
import com.example.bungae.singleton.GetMyProfile
import com.example.bungae.ui.message.adapter.ChattingRoomAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChattingRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChattingRoomDetailBinding
    private lateinit var chatInfoData: ChatInfoData
    private var myProfileData: ProfileData? = null
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var adapter: ChattingRoomAdapter

    private val chattingRoomViewModel by lazy {
        ChattingRoomViewModel(auth, db)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingRoomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatInfoData = intent.getSerializableExtra("profile data") as ChatInfoData

        if (myProfileData == null) {
            GetMyProfile.getMyProfile()
        }


        adapter = ChattingRoomAdapter()

        binding.recyclerviewChatting.adapter = adapter
        binding.recyclerviewChatting.layoutManager = LinearLayoutManager(this)

        binding.textViewNickname.text = chatInfoData.nickname

        chattingRoomViewModel.getChatData(chatInfoData.uid)

        binding.imageMessageSend.setOnClickListener {
            chattingRoomViewModel.setChatData(
                chatInfoData.uid,
                chatInfoData.nickname,
                myProfileData?.nickname,
                binding.editMessageText.text.toString()
            )
            binding.editMessageText.text = null
        }

        chattingRoomViewModel.chatData.observe(this, Observer {
            Log.e("chatData", it.toString())
            adapter.submitList(it)
            binding.recyclerviewChatting.scrollToPosition(it.size - 1)
        })

        GetMyProfile.myProfile.observe(this, Observer {
            myProfileData = it
        })

    }
}