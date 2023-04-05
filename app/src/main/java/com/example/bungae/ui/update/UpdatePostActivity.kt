package com.example.bungae.ui.update

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bungae.R
import com.example.bungae.data.ItemData
import com.example.bungae.databinding.ActivityUpdatePostBinding
import com.example.bungae.ui.post.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class UpdatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdatePostBinding

    private var uriInfo: Uri? = null

    private val updatePostViewModel: PostViewModel by viewModels()

    private val permissionList = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES
    )

    private val requestMultiplePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.forEach {
                if (!it.value) {
                    Toast.makeText(this, "${it.key}권한 허용 필요", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            openDialog(this)
        }

    private val readImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let { uri ->
                contentResolver?.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Glide.with(this).load(uri).into(binding.imgUpdateLoad)
                uriInfo = uri
            }
        }

    private val getTakePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                uriInfo.let { Glide.with(this).load(uriInfo).into(binding.imgUpdateLoad) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = intent.getSerializableExtra("item") as ItemData

        updatePostViewModel.sendItem(item)

        binding.btnUpdateAddImage.setOnClickListener {
            requestMultiplePermission.launch(permissionList)
        }

        binding.btnUpdateCompletion.setOnClickListener {
            if (uriInfo == null) {
                updatePostViewModel.updateItem(
                    uriInfo = item.imageUrl,
                    title = binding.editUpdatePostTitle.text.toString(),
                    content = binding.editUpdatePostContent.text.toString(),
                    category = binding.spinnerUpdateCategory.selectedItem.toString(),
                    address = binding.tvUpdateMap.text.toString(),
                    date = item.date
                )
            } else {
                updatePostViewModel.updateImageToFirebase(uriInfo!!, item.date)
            }
        }

        updatePostViewModel.imageUrl.observe(this, androidx.lifecycle.Observer {
            updatePostViewModel.updateItem(
                uriInfo = it.toString(),
                title = binding.editUpdatePostTitle.text.toString(),
                content = binding.editUpdatePostContent.text.toString(),
                category = binding.spinnerUpdateCategory.selectedItem.toString(),
                address = binding.tvUpdateMap.text.toString(),
                date = item.date
            )
        })

        updatePostViewModel.item.observe(this, androidx.lifecycle.Observer {
            Glide.with(this).load(it.imageUrl).into(binding.imgUpdateLoad)
            binding.editUpdatePostTitle.text = Editable.Factory.getInstance().newEditable(it.title)
            binding.editUpdatePostContent.text =
                Editable.Factory.getInstance().newEditable(it.content)
        })

        updatePostViewModel.success.observe(this, androidx.lifecycle.Observer {
            if (it) {
                Toast.makeText(this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "게시글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })

        updatePostViewModel.blankCheck.observe(this, androidx.lifecycle.Observer {
            if (!it) {
                Toast.makeText(this, "빈칸을 전부 채워주세요.", Toast.LENGTH_SHORT).show()
            }
        })

        updatePostViewModel.map.observe(this, androidx.lifecycle.Observer {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("result", it)
            })
            finish()
        })

        updatePostViewModel.coordinates.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                binding.tvUpdateMap.text = it.getAddressLine(0)
            }
        })
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
