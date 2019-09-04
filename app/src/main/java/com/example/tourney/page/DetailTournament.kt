package com.example.tourney.page

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tourney.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.detail_tournament.*
import kotlinx.android.synthetic.main.nav_header_dashboard.*

class DetailTournament : AppCompatActivity() {

    lateinit var dbRef: DatabaseReference

    lateinit var fAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_tournament)
        setSupportActionBar(toolbarDetail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fAuth = FirebaseAuth.getInstance()

        val name = intent.getStringExtra("nama_user")
        val iduser = intent.getStringExtra("iduser")
        val id_tour = intent.getStringExtra("id_tournament")
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
        Glide.with(this@DetailTournament)
            .load(foto).error(R.drawable.logo)
            .centerCrop().into(profilePanitia_detail)
        Picasso.with(this@DetailTournament)
            .load(brosur).error(R.drawable.logo)
            .centerCrop().resize(200, 200)
            .into(brosurT_detail)
        FirebaseDatabase.getInstance().getReference("tournament/$id_tour")
            .child("arena").addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {
                    arena_detail.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        FirebaseDatabase.getInstance().getReference("tournament/$id_tour")
            .child("system").addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {
                    system_detail.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        FirebaseDatabase.getInstance().getReference("tournament/$id_tour")
            .child("price").addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {
                    priceDetail.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        profilePanitia_detail.setOnClickListener {
            val intent = Intent(this@DetailTournament, DetailFoto::class.java)
            intent.putExtra("foto", foto)
            startActivity(intent)
        }
        brosurT_detail.setOnClickListener {
            val intent = Intent(this@DetailTournament, DetailFoto::class.java)
            intent.putExtra("foto", brosur)
            startActivity(intent)
        }
        if (fAuth.currentUser?.uid != iduser){
            if (tersisa!!.toInt() == 0)
            {
                ikutTourDetail.visibility = View.GONE
            }else{
                ikutTourDetail.setOnClickListener {
                    val intent = Intent(this@DetailTournament, IkutTour::class.java)
                    intent.putExtra("iduser2", iduser)
                    intent.putExtra("iduser1", fAuth.currentUser?.uid)
                    intent.putExtra("idtour", id_tour)
                    startActivity(intent)
                }
            }
            name_detail.setOnClickListener {
                val intent = Intent(this@DetailTournament, ProfilePerson::class.java)
                intent.putExtra("iduser", iduser)
                startActivity(intent)
            }
        }else{
            ikutTourDetail.visibility = View.GONE
            updateSlotDetail.visibility = View.VISIBLE
            updateSlotDetail.setOnClickListener {
                val builder = AlertDialog.Builder(this@DetailTournament)
                val view = LayoutInflater.from(this@DetailTournament).inflate(R.layout.update_slot, null)
                builder.setView(view)
                builder.setMessage("Update Slot")
                val tss = tersisa
                val stk = slot
                val etstok = view.findViewById<EditText>(R.id.et_stok)
                etstok.setText(tss)

                builder.setPositiveButton("No") { dialog, i ->
                    dialog.dismiss()
                }
                builder.setNegativeButton("Yes") { dialog, i ->
                    val sl = slot!!.toString()
                    val stok = etstok.text.toString()
                    if (stok.toInt() > sl.toInt()) {
                        Toast.makeText(
                            this@DetailTournament,
                            "Melebihi Slot",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        dbRef = FirebaseDatabase.getInstance()
                            .getReference("tournament")
                        dbRef.child("$id_tour/tersisa").setValue(stok)
                        dbRef.push()
                        Toast.makeText(
                            this@DetailTournament,
                            "Update Success",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
            name_detail.setOnClickListener {
                val intent = Intent(this@DetailTournament, Profile::class.java)
                startActivity(intent)
            }
        }

    }
}

