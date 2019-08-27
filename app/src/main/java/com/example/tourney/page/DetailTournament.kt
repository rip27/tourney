package com.example.tourney.page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tourney.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.detail_tournament.*
import kotlinx.android.synthetic.main.nav_header_dashboard.*

class DetailTournament : AppCompatActivity() {

    lateinit var fAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_tournament)
        setSupportActionBar(toolbarDetail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fAuth = FirebaseAuth.getInstance()

        val name = intent.getStringExtra("nama_user")
        val foto = intent.getStringExtra("foto_profile")
        val nameT = intent.getStringExtra("nameT")
        val brosur = intent.getStringExtra("brosur")
        val category = intent.getStringExtra("category")
        val domisili = intent.getStringExtra("domisili")
        val peserta = intent.getStringExtra("peserta")
        val slot = intent.getStringExtra("slot")
        val tersisa = intent.getStringExtra("tersisa")
        val dibuka = intent.getStringExtra("dibuka")
        val ditutup = intent.getStringExtra("ditutup")

        nameT_detail.text = nameT
        domisiliT_detail.text = domisili
        name_detail.text = name
        category_detail.text = category
        arena_detail.text = category
        peserta_detail.text = peserta
        tersisa_detail.text = tersisa
        slot_detail.text = slot
        dibuka_detail.text = dibuka
        ditutup_detail.text = ditutup
        Picasso.with(this@DetailTournament)
            .load(foto).error(R.drawable.logo)
            .centerCrop().resize(50, 50)
            .into(profilePanitia_detail)

        Picasso.with(this@DetailTournament)
            .load(brosur).error(R.drawable.logo)
            .centerCrop().resize(200, 200)
            .into(brosurT_detail)
        FirebaseDatabase.getInstance().getReference("tournament/${fAuth.uid}")
            .child("arena").addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {
                    arena_detail.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        FirebaseDatabase.getInstance().getReference("tournament/${fAuth.uid}")
            .child("system").addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {
                    system_detail.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }
}

