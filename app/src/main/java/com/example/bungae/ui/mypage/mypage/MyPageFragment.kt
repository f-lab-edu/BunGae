package com.example.bungae.ui.mypage.mypage

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bungae.R
import com.example.bungae.databinding.FragmentMypageBinding
import com.example.bungae.ui.account.login.LoginActivity
import com.example.bungae.ui.mypage.mypost.MyPostActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : Fragment() {

    @Inject
    lateinit var auth: FirebaseAuth

    private var _binding: FragmentMypageBinding? = null

    private val binding get() = _binding!!

    private var uriInfo: Uri? = null

    private val myPageViewModel: MyPageViewModel by viewModels()

    private val permissionList = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES
    )

    private val requestMultiplePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.forEach {
                if (!it.value) {
                    Toast.makeText(context, "${it.key}권한 허용 필요", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private val readImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let { uri ->
                activity?.contentResolver?.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Glide.with(this).load(uri).into(binding.imageMypageProfile)
                uriInfo = uri
            }
        }

    private val getTakePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                uriInfo.let { Glide.with(this).load(uriInfo).into(binding.imageMypageProfile) }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.editUpdateProfileNickname.visibility = View.GONE
        binding.btnUpdateProfileNickname.visibility = View.GONE

        myPageViewModel.getNickname()

        binding.btnEditNickname.setOnClickListener {
            binding.btnEditNickname.visibility = View.GONE
            binding.editUpdateProfileNickname.visibility = View.VISIBLE
            binding.btnUpdateProfileNickname.visibility = View.VISIBLE
        }

        binding.btnUpdateProfileNickname.setOnClickListener {
            myPageViewModel.checkNickName(binding.editUpdateProfileNickname.text.toString())
        }

        binding.imageMypageProfile.setOnClickListener {
            requestMultiplePermission.launch(permissionList)
            openQuestionDialog()
            myPageViewModel.getProfileImage()
        }

        myPageViewModel.task.observe(viewLifecycleOwner, Observer {
            Glide.with(this).load(it).circleCrop().into(binding.imageMypageProfile)
        })

        myPageViewModel.checkNickname.observe(viewLifecycleOwner, Observer {
            if (!it) {
                Toast.makeText(context, "중복된 값이 있습니다.", Toast.LENGTH_SHORT).show()
            } else {
                myPageViewModel.updateNickName(binding.editUpdateProfileNickname.text.toString())
                Toast.makeText(context, "닉네임이 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()

                // 닉네임이 수정된 후 visibility 변경
                binding.btnEditNickname.visibility = View.VISIBLE
                binding.editUpdateProfileNickname.visibility = View.GONE
                binding.btnUpdateProfileNickname.visibility = View.GONE

                // 변경된 닉네임 뷰에 적용
                myPageViewModel.getNickname()
            }
        })

        myPageViewModel.loadImageSuccess.observe(viewLifecycleOwner, Observer {
            if (!it) {
                Toast.makeText(context, "프로필 사진을 등록해주세요", Toast.LENGTH_SHORT).show()
            }
        })

        myPageViewModel.listProfileData.observe(viewLifecycleOwner, Observer {
            binding.data = it
        })

        binding.btnGetMyitem.setOnClickListener {
            startActivity(Intent(activity, MyPostActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        return root
    }

    private fun createImageFile(): Uri? {
        val now = SimpleDateFormat("yy_MM_dd_HH_mm", Locale.KOREA).format(Date())
        val content = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "img_$now.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        }
        return activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content)
    }

    private fun openQuestionDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("사진 수정")
        builder.setMessage("프로필 사진을 수정하시겠습니까?")
        builder.setNegativeButton("아니요", null)
        builder.setPositiveButton("네", object: DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                openLoadImageDialog(requireContext())
            }
        })
        builder.show()
    }

    private fun openLoadImageDialog(context: Context) {
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
            openUpdateImageDialog()
            dialog.dismiss()
        }
        fileAddBtn.setOnClickListener {
            readImage.launch(intent)
            openUpdateImageDialog()
            dialog.dismiss()
        }
    }

    private fun openUpdateImageDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("사진 수정")
        builder.setMessage("선택한 사진으로 수정하시겠습니까?")
        builder.setNegativeButton("아니요", null)
        builder.setPositiveButton("네", object: DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                uriInfo?.let { myPageViewModel.updateImageToFirebase(it) }
            }
        })
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}