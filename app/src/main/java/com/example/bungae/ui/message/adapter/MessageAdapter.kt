package com.example.bungae.ui.message.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bungae.R
import com.example.bungae.data.ChatInfoData
import com.example.bungae.data.ChatListData
import com.example.bungae.databinding.ItemMessageBinding
import com.example.bungae.ui.message.chatting_room.ChattingRoomActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class MessageAdapter (
    val auth: FirebaseAuth
): ListAdapter<ChatListData, MessageAdapter.MyViewHolder>(diffUtil) {

    class MyViewHolder (
        private val binding: ItemMessageBinding,
        val auth: FirebaseAuth
        ) : RecyclerView.ViewHolder(binding.root){
        val root = binding.root

        fun bind(item: ChatListData) {
            if (item.uid != auth.currentUser!!.uid) {
                binding.tvMessageNickname.text = item.senderNickname
                binding.tvMessage.text = item.message
                Glide.with(itemView).load(R.drawable.ic_baseline_person_24).into(binding.imageProfile)
            } else {
                binding.tvMessageNickname.text = item.receiverNickname
                binding.tvMessage.text = item.message
                Glide.with(itemView).load(R.drawable.ic_baseline_person_24).into(binding.imageProfile)
            }

            itemView.setOnClickListener {
                if (item.uid != auth.currentUser!!.uid) {
                    val chatInfoData = ChatInfoData(item.uid, item.senderNickname)
                    Intent(root.context, ChattingRoomActivity::class.java).apply {
                        putExtra("profile data", chatInfoData)
                    }.run { root.context.startActivity(this) }
                } else {
                    val chatInfoData = ChatInfoData(item.uid, item.receiverNickname)
                    Intent(root.context, ChattingRoomActivity::class.java).apply {
                        putExtra("profile data", chatInfoData)
                    }.run { root.context.startActivity(this) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemMessageBinding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, auth)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatListData>() {

            override fun areItemsTheSame(oldItem: ChatListData, newItem: ChatListData): Boolean {
                return oldItem.timestamp == newItem.timestamp
            }

            override fun areContentsTheSame(oldItem: ChatListData, newItem: ChatListData): Boolean {
                return oldItem == newItem
            }
        }
    }
}