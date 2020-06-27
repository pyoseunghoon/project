package com.pyo.safe_guard


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyo.safe_guard.navigation.model.UserDto
import kotlinx.android.synthetic.main.activity_register01.*


class Register01Activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register01)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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

        if (check_pw.text.toString().isEmpty()) {
            register_pw.error = "패스워드 확인을 입력해주세요."
            register_pw.requestFocus()
            return
        }

        if(register_pw.text.toString() != check_pw.text.toString()) {
            Toast.makeText(this, "패스워드와 패스워드 확인이 일치하지 않습니다.", Toast.LENGTH_LONG).show()
            return
        }

        auth.createUserWithEmailAndPassword(register_em.text.toString(), register_pw.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        // DB에 회원정보 저장
                        var userDto = UserDto()

                        userDto.email = register_em.text.toString()
                        userDto.password = register_pw.text.toString()
                        userDto.name = register_name.text.toString()
                        userDto.timestamp = System.currentTimeMillis()
                        userDto.uid = auth.currentUser?.uid


                        firestore.collection("users").document().set(userDto)

                        // 로그인
                        Toast.makeText(this, "회원가입이 완료됐습니다.", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()

                    } else {
                        // 에러 메세지 출력
                        Toast.makeText(baseContext, "회원인증에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
    }

}



