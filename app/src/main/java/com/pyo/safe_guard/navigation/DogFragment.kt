package com.pyo.safe_guard.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pyo.safe_guard.R
import com.pyo.safe_guard.model.DogDto
import kotlinx.android.synthetic.main.activity_register02.*
import java.text.SimpleDateFormat
import java.util.*

class DogFragment : Fragment() {
    var PICK_IAMGE_FROM_ALBUM = 0
    var photoUri: Uri? = null
//    private lateinit var auth: FirebaseAuth
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var storage: FirebaseStorage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_dog, container, false)

        // 초기화
//        storage = FirebaseStorage.getInstance()
//        auth = FirebaseAuth.getInstance()
//        firestore = FirebaseFirestore.getInstance()

        register_btn.setOnClickListener {
            signUpDogInfo()
        }

        // 앨범 열기
        upload_img.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IAMGE_FROM_ALBUM)
        }

        return view
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
                Toast.makeText(context, "오류발생", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signUpDogInfo() {

        // 파일 이름 생성
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = FirebaseStorage.getInstance().reference.child("images").child(imageFileName)

        storageRef.putFile(photoUri!!).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                var dogDto = DogDto()

                dogDto.name = petName_et.text.toString()
                dogDto.kind = petKind_et.text.toString()
                dogDto.age = petAge_et.text.toString()
                dogDto.birth = petBirth_et.text.toString()

                when(sex_radio_group.checkedRadioButtonId) {
                    R.id.sex_male -> dogDto.sex = "수컷"
                    R.id.sex_female -> dogDto.sex = "암컷"
                }

                when(surgery_radio_group.checkedRadioButtonId) {
                    R.id.surgery_yes -> dogDto.surgery = "예"
                    R.id.surgery_no -> dogDto.surgery = "아니오"
                }

                when(socialLevel_radio_group.checkedRadioButtonId) {
                    R.id.social_high ->dogDto.socialLevel = "상"
                    R.id.social_middle ->dogDto.socialLevel = "중"
                    R.id.social_low ->dogDto.socialLevel = "하"
                }

                // 이미지의 다운로드 url 삽입
                dogDto.imageUrl = uri.toString()

                // 유저의 uid 삽입
                dogDto.uid = FirebaseAuth.getInstance().currentUser?.uid

                FirebaseFirestore.getInstance().collection("dogs")?.document()?.set(dogDto)

                Toast.makeText(context, "등록완료", Toast.LENGTH_LONG).show()
            }
        }
    }
}