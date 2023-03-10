package com.example.bungae.ui.detail

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.bungae.data.ChatInfoData
import com.example.bungae.data.ItemData
import com.example.bungae.data.ProfileData
import com.example.bungae.databinding.ActivityDetailBinding
import com.example.bungae.ui.message.ChattingRoomActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemData
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var profileData: ProfileData

    private val detailViewModel by lazy {
        DetailViewModel( db)
    }

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

        detailViewModel.porfileImage.observe(this, Observer {
            Glide.with(this).load(it).circleCrop().into(binding.imageDetailProfile)
        })

        detailViewModel.deleteResult.observe(this, Observer {
            if (it) {
                Toast.makeText(this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
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
//                putExtra("item data", item)
                putExtra("profile data", chatInfoData)
            }.run { startActivity(this) }
        }
    }

    private fun askToDeleteItem() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("????????? ??????")
        builder.setMessage("???????????? ?????????????????????????")
        builder.setNegativeButton("?????????", null)
        builder.setPositiveButton("???", object: DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                detailViewModel.deleteItem(item)
            }
        })
        builder.show()
    }
}