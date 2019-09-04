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
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.tourney.R
import com.example.tourney.data.Pref
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.ikut_tour.*
import kotlinx.android.synthetic.main.profile.*
import kotlinx.android.synthetic.main.tambah_tour.*
import java.io.IOException
import java.util.*

class IkutTour : AppCompatActivity() {

    var value = 0.0
    lateinit var pref: Pref
    lateinit var dbRef: DatabaseReference
    lateinit var filePathImage: Uri
    val REQUEST_CODE_IMAGE = 10002
    val PERMISSION_RC = 10003
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ikut_tour)
        pref = Pref(this)
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference
        backa.setOnClickListener {
            onBackPressed()
        }
        val iduser1 = intent.getStringExtra("iduser1")
        val iduser2 = intent.getStringExtra("iduser2")
        val idtour = intent.getStringExtra("idtour")

        val dataUserRef = FirebaseDatabase.getInstance().getReference("user")
        val dataTourRef = FirebaseDatabase.getInstance().getReference("tournament")

        dataUserRef.child("$iduser1/name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                atasNama.text = p0.value.toString()
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        dataUserRef.child("$iduser2/name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                penyelenggaraTournament.text = p0.value.toString()
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        dataTourRef.child("$idtour/nameT").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                namaTournament.text = p0.value.toString()
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        dataTourRef.child("$idtour/price").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                priceTournament.text = p0.value.toString()
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        buktiTransfer.setOnClickListener {
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

        bt_ikutTour.setOnClickListener {
            val atasnama = iduser1!!.toString()
            val namat = idtour!!.toString()
            val panitia = iduser2!!.toString()

            if (atasnama.isNotEmpty() || namat.isNotEmpty() ||
                panitia.isNotEmpty()
            ) {
                addJoinTourToFirebase(atasnama, namat, panitia)
                Handler().postDelayed({
                    onBackPressed()
                }, 1000)
                Toast.makeText(
                    this,
                    "Success Upload",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Fill Data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun addJoinTourToFirebase(atasnama: String, namat: String, panitia: String) {
        val nameXXX = UUID.randomUUID().toString()
        val uidJoin = UUID.randomUUID().toString()
        val uid = atasnama
        val storageRef: StorageReference = storageReference
            .child("images/$uid/$nameXXX.${GetFileExtension(filePathImage)}")
        storageRef.putFile(filePathImage).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                dbRef = FirebaseDatabase.getInstance().getReference("join/$namat/$uidJoin")
                dbRef.child("buktitf").setValue(it.toString())
                dbRef.child("idtour").setValue(namat)
                dbRef.child("iduser1").setValue(uid)
                dbRef.child("idjoin").setValue(uidJoin)
                dbRef.child("iduser2").setValue(panitia)
                dbRef.child("status").setValue("PENDING")
            }
            progressIkut.visibility = View.GONE
            bt_ikutTour.visibility = View.VISIBLE
        }.addOnFailureListener {
            Log.e("TAG_ERROR", it.message)
        }.addOnProgressListener { taskSnapshot ->
            value = (100.0 * taskSnapshot
                .bytesTransferred / taskSnapshot.totalByteCount)
            bt_ikutTour.visibility = View.GONE
            progressIkut.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_RC -> {
                if (grantResults.isEmpty() ||
                    grantResults[0] == PackageManager.PERMISSION_DENIED
                ) {
                    Toast.makeText(
                        this,
                        "Ditolak",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    imageChooser()
                }
            }
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
                        .override(175, 175)
                        .centerCrop().into(buktiTransfer)
                } catch (x: IOException) {
                    x.printStackTrace()
                }
            }
        }
    }

    fun GetFileExtension(uri: Uri): String? {
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }
}
