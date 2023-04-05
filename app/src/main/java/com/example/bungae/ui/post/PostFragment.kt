package com.example.bungae.ui.post

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.bungae.R
import com.example.bungae.databinding.FragmentPostBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding
        get() = _binding!!

    private var uriInfo: Uri? = null

    private val postViewModel by lazy {
        ViewModelProvider(requireActivity()).get(PostViewModel::class.java)
    }
    private lateinit var navController: NavController

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
            openDialog(requireContext())
        }

    private val readImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let { uri ->
                activity?.contentResolver?.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Glide.with(this).load(uri).into(binding.imgPostLoad)
                uriInfo = uri
            }
        }

    private val getTakePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                uriInfo.let { Glide.with(this).load(uriInfo).into(binding.imgPostLoad) }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        // 주소값 받아온 후 텍스트 뷰 변경
//        childFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { requestKey, bundle ->
//            val address = bundle.getString("address")
//            binding.tvMap.text = address
//        }

        binding.btnCompletion.setOnClickListener {
            // 이미지를 등록 안 했을 시
            if (uriInfo == null) {
                postViewModel.insertFireStorage(
                    title = binding.editPostTitle.text.toString(),
                    content = binding.editPostContent.text.toString(),
                    category = binding.spinnerCategory.selectedItem.toString(),
                    address = binding.tvMap.text.toString(),
                    imageUrl = "null"
                )
            } else {
                postViewModel.uploadImageToFirebase(uriInfo)
            }
        }

        binding.btnPostAddImage.setOnClickListener {
            requestMultiplePermission.launch(permissionList)
        }

        // 이미지를 등록할 시
        postViewModel.imageUrl.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            postViewModel.insertFireStorage(
                title = binding.editPostTitle.text.toString(),
                content = binding.editPostContent.text.toString(),
                category = binding.spinnerCategory.selectedItem.toString(),
                address = binding.tvMap.text.toString(),
                imageUrl = it
            )
        })

        postViewModel.success.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                Toast.makeText(context, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.navigation_home)
            } else {
                Toast.makeText(context, "게시글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })

        postViewModel.blankCheck.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (!it) {
                Toast.makeText(context, "빈칸을 전부 채워주세요.", Toast.LENGTH_SHORT).show()
            }
        })

        postViewModel.coordinates.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                binding.tvMap.text = it.getAddressLine(0)
            }
        })
    }

    // 프레그먼트에 프레그먼트 얹기
//    private fun addMapFragment() {
//        childFragmentManager.beginTransaction()
//            .replace(R.id.fragment_post, PostMapFragment())
//            .setReorderingAllowed(true)
//            .addToBackStack(null)
//            .commit()
//    }

    private fun createImageFile(): Uri? {
        val now = SimpleDateFormat("yy_MM_dd_HH_mm", Locale.KOREA).format(Date())
        val content = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "img_$now.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        }
        return activity?.contentResolver?.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            content
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}