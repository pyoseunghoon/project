package com.pyo.safe_guard

import android.annotation.SuppressLint
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
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_reigiser_acitivity.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)





        // 파이어베이스 통합 관리 객체 생성
        auth = FirebaseAuth.getInstance()

        sign_Up.setOnClickListener {
            startActivity(Intent(this, Register01Activity::class.java))
            finish()
        }

        loginBtn.setOnClickListener {
            doLogin()
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 구글 승인 정보 가져오기
        if(requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result!!.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account)
            } else {

            }
        }
    }

    // 파이어베이스 인증
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if(task.isSuccessful){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, "로그인에 실패했습니다." ,Toast.LENGTH_LONG).show()
            }
        }
    }

    // 이메일로 로그인
    private fun doLogin() {
        if (sign_Up_Email.text.toString().isEmpty()) {
            sign_Up_Email.error = "이메일을 입력해주세요."
            sign_Up_Email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(sign_Up_Email.text.toString()).matches()) {
            sign_Up_Email.error = "유효한 이메일을 입력해주세요."
            sign_Up_Email.requestFocus()
            return
        }

        if (sign_Up_Password.text.toString().isEmpty()) {
            sign_Up_Password.error = "패스워드를 입력해주세요."
            sign_Up_Password.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(sign_Up_Email.text.toString(), sign_Up_Password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(baseContext, "로그인에 실패했습니다." ,Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {

        if (currentUser != null) {
            if(currentUser.isEmailVerified) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(
                    baseContext, "이메일 인증이 필요합니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
//            Toast.makeText(
//                baseContext, "로그인이 실패했습니다.",
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }
}
