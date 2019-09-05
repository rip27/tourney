package com.example.tourney.page

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tourney.R
import com.example.tourney.adapter.JoinAdapter
import com.example.tourney.adapter.TournamentProfileAdapter
import com.example.tourney.data.Pref
import com.example.tourney.model.JoinModel
import com.example.tourney.model.TournamentModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.join.*
import kotlinx.android.synthetic.main.profile.*
import java.util.ArrayList

class Join : AppCompatActivity() {

    lateinit var preferences: Pref
    lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var joinAdapter: JoinAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var list: MutableList<JoinModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join)
        setSupportActionBar(toolbarJoin)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        preferences = Pref(this)
        fAuth = FirebaseAuth.getInstance()

        val userid = fAuth.currentUser?.uid

        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rcv_join)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)
        dbRef = FirebaseDatabase.getInstance()
            .reference.child("join/")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                list = ArrayList()
                p0.children.forEach {
                    it.children.forEach {it2->
                        val addDataAll = it2.getValue(
                            JoinModel::class.java)
                        addDataAll!!.key = it2.key
                        if (addDataAll.iduser1.toString() == userid) {
                            list.add(addDataAll)
                        }
                    }
                }
                joinAdapter = JoinAdapter(this@Join, list)
                recyclerView!!.adapter = joinAdapter
//                for (dataSnapshot in p0.children) {
//                    val addDataAll = dataSnapshot.getValue(
//                        JoinModel::class.java
//                    )
//                    addDataAll!!.key = dataSnapshot.key
//                    list.add(addDataAll)
//                    joinAdapter = JoinAdapter(this@Join, list)
//                    recyclerView!!.adapter = joinAdapter
//                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })

    }
}
