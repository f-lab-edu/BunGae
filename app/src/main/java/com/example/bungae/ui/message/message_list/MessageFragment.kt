package com.example.bungae.ui.message.message_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bungae.data.ChatListData
import com.example.bungae.data.ChatModel
import com.example.bungae.databinding.FragmentMessageBinding
import com.example.bungae.ui.message.adapter.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val adapter by lazy { MessageAdapter() }
    private lateinit var map: Map<String, List<ChatModel>>
    private var list: MutableList<ChatListData> = mutableListOf()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val binding
        get() = _binding!!

    private val messageViewModel by lazy {
        MessageViewModel(auth, db)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recyclerviewMessage.adapter = adapter
        binding.recyclerviewMessage.layoutManager = LinearLayoutManager(activity)
//        adapter.submitList(list)

        messageViewModel.getMyChatList()

        messageViewModel.chatList.observe(viewLifecycleOwner, Observer {
            // 닉네임 기준으로 묶음
            map = it.groupBy { it.comments.get("comment")!!.senderNickname }

            // 내림차순으로 정렬된 날짜를
            for (i in map) {
                var cnt = 0
                list.add(
                    ChatListData(
                        senderNickname = i.key,
                        uid = i.value.get(cnt).comments.get("comment")!!.uid,
                        receiverNickname1 = i.value.get(cnt).comments.get("comment")!!.receiverNickname1 ,
                        message = i.value.get(cnt).comments.get("comment")!!.message,
                        timestamp = i.value.get(cnt).comments.get("comment")!!.timestamp
                    )
                )
                cnt++
            }
            adapter.submitList(list)
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}