package com.example.tourney.page

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.tourney.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.detail_join.*
import kotlinx.android.synthetic.main.join.*

class DetailJoin : AppCompatActivity() {

    lateinit var dbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_join)
        setSupportActionBar(tulbarDetail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val iduser1 = intent.getStringExtra("iduser1")
        val iduser2 = intent.getStringExtra("iduser2")
        val idtour = intent.getStringExtra("idtour")
        val status = intent.getStringExtra("status")
        val buktitf = intent.getStringExtra("buktitf")

        val dataUser = FirebaseDatabase.getInstance().getReference("user")
        val dataTour = FirebaseDatabase.getInstance().getReference("tournament")

        dataUser.child("$iduser1/name").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                atasNamaD.text = p0.value.toString()
            }

        })
        dataUser.child("$iduser2/name").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                penyelenggaraTournamentD.text = p0.value.toString()
            }

        })
        dataTour.child("$idtour/nameT").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                namaTournamentD.text = p0.value.toString()
            }

        })
        dataTour.child("$idtour/price").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                priceTournamentD.text = p0.value.toString()
            }

        })

        Glide.with(this@DetailJoin).load(buktitf).into(buktiTransferD)

        if (status == "PENDING"){

        }else if (status == "APPROVED"){
            st_pend.visibility = View.GONE
            st_app.visibility = View.VISIBLE
            st_rej.visibility = View.GONE
        }else if (status == "REJECTED"){
            st_pend.visibility = View.GONE
            st_app.visibility = View.GONE
            st_rej.visibility = View.VISIBLE
        }

    }
}
