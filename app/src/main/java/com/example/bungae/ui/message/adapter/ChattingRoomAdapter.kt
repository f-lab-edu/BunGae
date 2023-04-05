package com.example.bungae.ui.message.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bungae.data.ChatModel
import com.example.bungae.databinding.ItemChattingBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class ChattingRoomAdapter(
    private val uri: Uri?,
    private val auth: FirebaseAuth
    ) : ListAdapter<ChatModel, ChattingRoomAdapter.MyViewHolder>(diffUtil) {

    class MyViewHolder(
        private val binding: ItemChattingBinding,
        private var uri: Uri?,
        private val auth: FirebaseAuth
        ) :
        RecyclerView.ViewHolder(binding.root) {
        val root = binding.root

        fun bind(item: ChatModel) {
            if (item.comments.get("comment")?.uid == auth.currentUser?.uid) {
                binding.tvChattingNickname.text = item.comments.get("comment")?.senderNickname
                binding.tvChattingMessage.text = item.comments.get("comment")?.message
                binding.tvChattingTimestamp.text = item.comments.get("comment")?.timestamp
                binding.linearLayoutChat1.visibility = View.INVISIBLE
                binding.linearLayoutChat2.gravity = Gravity.RIGHT
            } else {
                Log.e("song", uri.toString())
                binding.tvChattingNickname.text = item.comments.get("comment")?.senderNickname
//                Glide.with(Activity()).load(uri).into(binding.imageChattingProfile)
                binding.tvChattingMessage.text = item.comments.get("comment")?.message
                binding.tvChattingTimestamp.text = item.comments.get("comment")?.timestamp
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemChattingBinding = ItemChattingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding, uri, auth)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<ChatModel>() {

            override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
                return oldItem.comments == newItem.comments
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}