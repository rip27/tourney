package com.example.tourney.page

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.tourney.R
import com.example.tourney.data.Pref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.edit_profile.*
import java.io.IOException

class EditProfile : AppCompatActivity() {

    lateinit var preferences: Pref
    val REQUEST_CODE_IMAGE = 10002
    val PERMISSION_RC = 10003
    var value = 0.0
    lateinit var filePathImage: Uri
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        preferences = Pref(this)
        fAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference

        editback.setOnClickListener {
            onBackPressed()
        }

        bteditProfil.setOnClickListener {
            when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ), PERMISSION_RC
                        )
                    } else {
                        imageChooser()
                    }
                }
                else -> {
                    imageChooser()
                }
            }
        }

        val userid = fAuth.currentUser?.uid
        val dataUserRef = FirebaseDatabase.getInstance().getReference("user/$userid")

        dataUserRef.child("profile").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Glide.with(this@EditProfile).load(p0.value.toString())
                    .centerCrop()
                    .error(R.drawable.logo)
                    .into(profileEdit)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        dataUserRef.child("foto_sampul").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Glide.with(this@EditProfile).load(p0.value.toString())
                    .centerCrop()
                    .error(R.drawable.fontblack)
                    .into(potosampuledit)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        dataUserRef.child("name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                editTextNameEdit.setText(p0.value.toString())
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        dataUserRef.child("phone").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                editTextPhoneEdit.setText(p0.value.toString())
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        saveProfile.setOnClickListener {
            saveData()
        }

    }

    private fun imageChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Image"),
            REQUEST_CODE_IMAGE
        )
    }
    fun GetFileExtension(uri: Uri): String? {
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_CODE_IMAGE -> {
                filePathImage = data?.data!!
                try {
                    val bitmap: Bitmap = MediaStore
                        .Images.Media.getBitmap(
                        this.contentResolver, filePathImage
                    )
                    Glide.with(this).load(bitmap)
                        .override(100, 100)
                        .centerCrop().into(profileEdit)
                } catch (x: IOException) {
                    x.printStackTrace()
                }
            }
        }
    }

    fun saveData() {
        val uidUser = fAuth.currentUser?.uid
        dbRef = FirebaseDatabase.getInstance().reference
        val eteditnamao = editTextNameEdit.text.toString()
        val eto = editTextPhoneEdit.text.toString()
        try {
            val storageRef: StorageReference = storageReference
                .child("profile/$uidUser/${preferences.getUIDD()}.${GetFileExtension(filePathImage)}")
            storageRef.putFile(filePathImage).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    dbRef.child("user/$uidUser/profile").setValue(it.toString())
                }
            }.addOnFailureListener {
                Log.e("TAG_ERROR", it.message)
            }.addOnProgressListener { taskSnapshot ->
                value = (100.0 * taskSnapshot
                    .bytesTransferred / taskSnapshot.totalByteCount)
            }
        } catch (e: UninitializedPropertyAccessException) {
            Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
        }
        dbRef.child("user/$uidUser/name").setValue(eteditnamao)
        dbRef.child("user/$uidUser/phone").setValue(eto)
        Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({
            onBackPressed()
        }, 1000)
    }
}
