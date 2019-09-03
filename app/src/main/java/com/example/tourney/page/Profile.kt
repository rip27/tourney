package com.example.tourney.page

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tourney.R
import com.example.tourney.adapter.TournamentProfileAdapter
import com.example.tourney.data.Pref
import com.example.tourney.model.TournamentModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.detail_foto.*
import kotlinx.android.synthetic.main.profile.*
import java.util.ArrayList

class Profile : AppCompatActivity() {

    lateinit var preferences: Pref
    lateinit var storageReference: StorageReference
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var tournamentProfileAdapter: TournamentProfileAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var list: MutableList<TournamentModel> = ArrayList()
    lateinit var filePathImage: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        preferences = Pref(this)
        fAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference

        val userid = fAuth.currentUser?.uid
        val dataUserRef = FirebaseDatabase.getInstance().getReference("user/$userid")


        dataUserRef.child("name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    tvNama.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        dataUserRef.child("phone").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    tvPhone.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        dataUserRef.child("profile").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Glide.with(this@Profile).load(p0.value.toString())
                    .centerCrop()
                    .error(R.drawable.logo)
                    .into(iv_profile)
                iv_profile.setOnClickListener {
                    Toast.makeText(this@Profile, "Profile", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Profile, DetailFoto::class.java)
                    intent.putExtra("foto", p0.value.toString())
                    startActivity(intent)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
        dataUserRef.child("foto_sampul").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Glide.with(this@Profile).load(p0.value.toString())
                    .centerCrop()
                    .error(R.drawable.fontblack)
                    .into(potosampul)
                potosampul.setOnClickListener {
                    Toast.makeText(this@Profile, "Sampul", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Profile, DetailFoto::class.java)
                    intent.putExtra("foto", p0.value.toString())
                    startActivity(intent)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        backk.setOnClickListener {
            onBackPressed()
        }

        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rcv_profile)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)
        dbRef = FirebaseDatabase.getInstance()
            .reference.child("tournament")
        dbRef.orderByChild("iduser").equalTo(userid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                list = ArrayList()
                for (dataSnapshot in p0.children) {
                    val addDataAll = dataSnapshot.getValue(
                        TournamentModel::class.java
                    )
                    addDataAll!!.key = dataSnapshot.key
                    list.add(addDataAll)
                    tournamentProfileAdapter = TournamentProfileAdapter(this@Profile, list)
                    recyclerView!!.adapter = tournamentProfileAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })

        btnEdit.setOnClickListener {
            startActivity(Intent(this@Profile, EditProfile::class.java))
        }
    }
}