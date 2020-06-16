package com.pyo.safe_guard

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_register01.*
import kotlinx.android.synthetic.main.activity_reigiser_acitivity.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Register01Activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register01)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance();

        btn_register.setOnClickListener {
            signUpUser()
        }

    }

    private fun signUpUser() {
        if (register_em.text.toString().isEmpty()) {
            register_em.error = "이메일을 입력해주세요."
            register_em.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(register_em.text.toString()).matches()) {
            register_em.error = "유효한 이메일을 입력해주세요."
            register_em.requestFocus()
            return
        }

        if (register_pw.text.toString().isEmpty()) {
            register_pw.error = "패스워드를 입력해주세요."
            register_pw.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(register_em.text.toString(), register_pw.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "회원가입이 완료됐습니다.", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, Register02Activity::class.java))
//                                finish()
                            }
                        }
                } else {
                    Toast.makeText(baseContext, "회원인증에 실패했습니다. 다시 시도해주세요.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
