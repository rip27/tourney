package com.example.tourney.page

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tourney.R
import com.example.tourney.data.Pref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.profile.*

class Proile : AppCompatActivity() {

    lateinit var fAuth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    lateinit var preferences: Pref
    lateinit var storageReference: StorageReference
    lateinit var firebaseStorage: FirebaseStorage

    lateinit var filePathImage: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        preferences = Pref(this)
        fAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference

        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
            .child("nama_user").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    tvNama.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        FirebaseDatabase.getInstance().getReference("user/${fAuth.uid}")
            .child("foto_profile").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    Glide.with(this@Proile).load(p0.value.toString())
                        .centerCrop()
                        .error(R.drawable.ic_launcher_background)
                        .into(iv_profile)
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }
}