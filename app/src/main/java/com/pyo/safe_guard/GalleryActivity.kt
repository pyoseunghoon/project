package com.pyo.safe_guard

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_gallery.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class GalleryActivity : AppCompatActivity() {
    var storage : FirebaseStorage? = null
    private val OPEN_GALLERY =1
    private var currentImageUrl : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        btn_picture.setOnClickListener{
            Log.d("gg","버튼 클릭")
            loadImage() }
        reg_btn.setOnClickListener {
            Log.d("gg","버튼 클릭")
            contentUpload() }
    }

    private fun loadImage() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent,OPEN_GALLERY)
    }

    fun contentUpload() {
        // 파일 이름 생성
        Log.d("url", currentImageUrl.toString())
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // 파일 업로드(Promise : 구글 권장 방식)
        storageRef?.putFile(currentImageUrl!!)
            ?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }?.addOnSuccessListener { uri ->
                setResult(Activity.RESULT_OK)
                Toast.makeText(this, "성공", Toast.LENGTH_LONG).show()
                finish()
            }
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == OPEN_GALLERY){
                currentImageUrl = data?.data
                try{
                    var bitmap : Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,currentImageUrl)
                    img_picture.setImageBitmap(bitmap)
                }catch (e:Exception) {
                    Toast.makeText(this, "$e",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Log.d("ActivityResult", "something wrong")
            }
        }

    }

}
