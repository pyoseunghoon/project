package com.pyo.safe_guard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.pyo.safe_guard.model.DogDto
import com.pyo.safe_guard.model.UserDto
import kotlinx.android.synthetic.main.activity_register02.*
import java.text.SimpleDateFormat
import java.util.*

class Register02Activity : AppCompatActivity() {
    var PICK_IAMGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register02)

        // 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        register_btn.setOnClickListener {
            signUpDogInfo()
        }

        // 앨범 열기
        upload_img.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IAMGE_FROM_ALBUM)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IAMGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                // 이미지의 경로
                photoUri = data?.data
                addphoto_img.setImageURI(photoUri)
            } else {
                // 취소 버튼 발생
                finish()
            }
        }
    }

    fun signUpDogInfo() {

        // 파일 이름 생성
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        auth?.createUserWithEmailAndPassword(intent.getStringExtra("email"), intent.getStringExtra("password"))
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        var userDto = UserDto()

                        userDto.email = intent.getStringExtra("email")
                        userDto.password = intent.getStringExtra("password")
                        userDto.name = intent.getStringExtra("name")
                        userDto.timestamp = System.currentTimeMillis()
                        userDto.uid = auth?.currentUser?.uid


                        firestore?.collection("users")?.document()?.set(userDto)

                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()


                    } else {
                        Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
                    }
                }

        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                var dogDto = DogDto()

                // 이미지의 다운로드 url 삽입
                dogDto.imageUrl = uri.toString()

                // 유저의 uid 삽입
                dogDto.uid = auth?.currentUser?.uid

                firestore?.collection("dogs")?.document()?.set(dogDto)

                Toast.makeText(this, "값 넣기 성공", Toast.LENGTH_LONG).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

    }
}
