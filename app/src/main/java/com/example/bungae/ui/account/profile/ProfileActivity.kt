package com.example.bungae.ui.account.profile

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.bungae.R
import com.example.bungae.databinding.ActivityWriteProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteProfileBinding
    private var uriInfo: Uri? = null
    private var nickNameCheckResult: Boolean = false

    private val profileViewModel: ProfileViewModel by viewModels()

    private var time: Long = 0

    private val callBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 1초 안에 두 번 누르면 앱 종료
            if (System.currentTimeMillis() > time + 1000) {
                time = System.currentTimeMillis()
                Toast.makeText(baseContext, "앱을 종료하려면 뒤로 버튼을 두 번 눌러주세요", Toast.LENGTH_SHORT).show()
                return
            }
            if (System.currentTimeMillis() <= time + 1000) {
                finishAffinity()
            }
        }
    }

    private val permissionList = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES
    )

    private val requestMultiplePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.forEach {
                if (!it.value) {
                    Toast.makeText(applicationContext, "${it.key}권한 허용 필요", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            openDialog(this)
        }

    private val readImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let { uri ->
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Glide.with(this).load(uri).into(binding.imageWriteProfile)
                uriInfo = uri
            }
        }

    private val getTakePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                uriInfo.let { Glide.with(this).load(uriInfo).into(binding.imageWriteProfile) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnWriteProfileCheck.setOnClickListener {
            profileViewModel.checkNickName(binding.editWriteProfileNickname.text.toString())
        }

        binding.imageWriteProfile.setOnClickListener {
            requestMultiplePermission.launch(permissionList)
        }

        binding.btnWriteProfileComplete.setOnClickListener {
            if (!nickNameCheckResult) {
                Toast.makeText(this, "닉네임이 중복됐는지 확인해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                if (uriInfo != null) {
                    profileViewModel.uploadImageToFirebase(
                        uriInfo,
                    )
                }

                if (binding.spinnerAge.selectedItem.toString() == "남자") {
                    profileViewModel.createProfile(
                        binding.editWriteProfileNickname.text.toString(),
                        binding.editWriteProfileAge.text.toString().toInt(),
                        true
                    )
                } else {
                    profileViewModel.createProfile(
                        binding.editWriteProfileNickname.text.toString(),
                        binding.editWriteProfileAge.text.toString().toInt(),
                        false
                    )
                }
            }
        }

        profileViewModel.checkNickname.observe(this, Observer {
            nickNameCheckResult = if (!it) {
                Toast.makeText(this, "중복된 값이 있습니다.", Toast.LENGTH_SHORT).show()
                false
            } else {
                Toast.makeText(this, "사용해도 되는 닉네임입니다.", Toast.LENGTH_SHORT).show()
                true
            }
        })

        profileViewModel.message.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        profileViewModel.checkFirestore.observe(this, Observer {
            if (it) {
                finish()
            }
        })

        this.onBackPressedDispatcher.addCallback(this, callBack)
    }

    private fun createImageFile(): Uri? {
        val now = SimpleDateFormat("yy_MM_dd_HH_mm", Locale.KOREA).format(Date())
        val content = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "img_$now.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content)
    }

    private fun openDialog(context: Context) {
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_choose_image, null)
        val dialogBuild = AlertDialog.Builder(context).apply {
            setView(dialogLayout)
        }
        val dialog = dialogBuild.create().apply { show() }

        val cameraAddBtn = dialogLayout.findViewById<Button>(R.id.btn_camera)
        val fileAddBtn = dialogLayout.findViewById<Button>(R.id.btn_file)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"

        cameraAddBtn.setOnClickListener {
            uriInfo = createImageFile()
            getTakePicture.launch(uriInfo)
            dialog.dismiss()
        }
        fileAddBtn.setOnClickListener {
            readImage.launch(intent)
            dialog.dismiss()
        }
    }
}