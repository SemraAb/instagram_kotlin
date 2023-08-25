package com.samra.instagramkotlin.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.samra.instagramkotlin.databinding.ActivityUploadBinding
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedImage : Uri?= null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()

        auth=Firebase.auth
        db = Firebase.firestore
        storage=Firebase.storage

    }
    fun upload(view:View){
        // universal uniques id
        var uuid = UUID.randomUUID()
        var imageName ="$uuid.jpg"
        var reference = storage.reference
        var imageReference = reference.child("images").child(imageName)

        if(selectedImage != null){
            imageReference.putFile(selectedImage!!).addOnSuccessListener {
                //download url -> firestore
                var uploadImageReference = storage.reference.child("images").child(imageName)
                uploadImageReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()

                    val postMap = hashMapOf<String , Any>()
                    postMap.put("downloadUrl", downloadUrl)
                    postMap.put("comment" , binding.commentText.text.toString())
                    postMap.put("userEmail" , auth.currentUser!!.email!!)
                    postMap.put("date" , Timestamp.now())

                    db.collection("Posts").add(postMap).addOnSuccessListener {
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this , it.localizedMessage , Toast.LENGTH_LONG).show()
                    }

                }
            }.addOnFailureListener {
                Toast.makeText(this , it.localizedMessage , Toast.LENGTH_LONG).show()
            }
        }
    }
    fun selectImage(view:View){
        if(ContextCompat.checkSelfPermission(this , android.Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this ,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view ,"Permission needed for gallery!" , Snackbar.LENGTH_INDEFINITE ).setAction("Give permission!"){
                    // request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                // request permission
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            var intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // startaActivity
            activityResultLauncher.launch(intentToGallery)

        }
    }
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if(result.resultCode == RESULT_OK){
                var intentFromResult = result.data
                if(intentFromResult != null){
                    selectedImage = intentFromResult.data
                    selectedImage?.let {
                        binding.imageView.setImageURI(it)
                    }
                }
            }
        }

        permissionLauncher  = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if(result){
                // permission granted
                var intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                // permission denied
                Toast.makeText(this , "Permission needed" , Toast.LENGTH_LONG).show()
            }
        }
    }
}