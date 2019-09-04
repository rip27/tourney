package com.example.tourney.page

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tourney.R
import com.example.tourney.adapter.JoinAdapter
import com.example.tourney.adapter.JoinmAdapter
import com.example.tourney.data.Pref
import com.example.tourney.model.JoinModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.join.*
import kotlinx.android.synthetic.main.join_mine.*
import java.util.ArrayList

class JoinMine : AppCompatActivity() {

    lateinit var preferences: Pref
    lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var joinmAdapter: JoinmAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var list: MutableList<JoinModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join_mine)

        setSupportActionBar(toolbarJoinM)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        preferences = Pref(this)
        fAuth = FirebaseAuth.getInstance()

        val idtour = intent.getStringExtra("idtour")
        val userid = fAuth.currentUser?.uid

        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rcv_join_mine)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)
        dbRef = FirebaseDatabase.getInstance()
            .reference.child("join")
//        dbRef.orderByChild("idtour").equalTo(idtour)
        dbRef.child(idtour)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    list = ArrayList()
                    for (dataSnapshot in p0.children) {
                        val addDataAll = dataSnapshot.getValue(
                            JoinModel::class.java
                        )
                        addDataAll!!.key = dataSnapshot.key
                        list.add(addDataAll)
                        joinmAdapter = JoinmAdapter(this@JoinMine, list)
                        recyclerView!!.adapter = joinmAdapter
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.e(
                        "TAG_ERROR", p0.message
                    )
                }
            })


    }
}
