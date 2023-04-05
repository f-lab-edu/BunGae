package com.example.bungae.ui.account.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.bungae.databinding.ActivitySignupBinding
import com.example.bungae.ui.account.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private val signUpActivity: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            signUpActivity.checkPasswd(binding.editId.text.toString(), binding.editPw.text.toString(), binding.editPw2.text.toString())
        }

        signUpActivity.message.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            if (it == "계정 생성을 완료했습니다.") {
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
        })
    }
}