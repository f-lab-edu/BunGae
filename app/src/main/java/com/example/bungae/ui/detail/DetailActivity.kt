package com.example.bungae.ui.detail

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bungae.data.ChatInfoData
import com.example.bungae.data.ItemData
import com.example.bungae.data.ProfileData
import com.example.bungae.databinding.ActivityDetailBinding
import com.example.bungae.ui.message.chatting_room.ChattingRoomActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemData

    private lateinit var profileData: ProfileData

    private val detailViewModel: DetailViewModel by viewModels()

    private val getList: ActivityResultLauncher<ItemData> =
        registerForActivityResult(ActivityContract()) { result: HashMap<String, String>? ->
            result?.let {
                binding.tvDetailTitle.text = it.get("title")
                binding.tvDetailContent.text = it.get("content")
                binding.tvDetailAddress.text = it.get("address")
                Glide.with(this).load(it.get("imageUrl")).into(binding.imageDetail)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        item = intent.getSerializableExtra("data") as ItemData
        binding.itemData = item

        detailViewModel.getProfileData(item.uid)

        detailViewModel.getProfileImage(item.uid)

        detailViewModel.profileDataList.observe(this, Observer {
            binding.profileData = it
            if (item.uid != auth.currentUser!!.uid) {
                binding.btnDetailItemUpdate.visibility = View.INVISIBLE
                binding.btnDetailItemDelete.visibility = View.INVISIBLE
            } else {
                binding.btnSendMessage.visibility = View.INVISIBLE
            }
            profileData = it
        })

        detailViewModel.profileImage.observe(this, Observer {
            Glide.with(this).load(it).circleCrop().into(binding.imageDetailProfile)
        })

        detailViewModel.deleteResult.observe(this, Observer {
            if (it) {
                Toast.makeText(this, "성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "아이템 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }

        })

        binding.btnDetailItemUpdate.setOnClickListener {
            getList.launch(item)
        }

        binding.btnDetailItemDelete.setOnClickListener {
            askToDeleteItem()
        }

        binding.btnSendMessage.setOnClickListener {
            val chatInfoData = ChatInfoData(profileData.uid, profileData.nickname)
            Intent(this, ChattingRoomActivity::class.java).apply {
                putExtra("profile data", chatInfoData)
            }.run { startActivity(this) }
        }
    }

    private fun askToDeleteItem() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("게시글 삭제")
        builder.setMessage("게시글을 삭제하시겠습니까?")
        builder.setNegativeButton("아니요", null)
        builder.setPositiveButton("네", object: DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                detailViewModel.deleteItem(item)
            }
        })
        builder.show()
    }
}