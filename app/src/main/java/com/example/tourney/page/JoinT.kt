package com.example.tourney.page

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tourney.R
import com.example.tourney.adapter.JoinAdapter
import com.example.tourney.adapter.JointAdapter
import com.example.tourney.data.Pref
import com.example.tourney.model.JoinModel
import com.example.tourney.model.TournamentModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.join_t.*
import java.util.ArrayList

class JoinT : AppCompatActivity() {

    lateinit var preferences: Pref
    lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var jointAdapter: JointAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var list: MutableList<TournamentModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join_t)
        setSupportActionBar(toolbarJoinT)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        preferences = Pref(this)
        fAuth = FirebaseAuth.getInstance()

        val userid = fAuth.currentUser?.uid

        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rcv_joinT)
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
                    jointAdapter = JointAdapter(this@JoinT, list)
                    recyclerView!!.adapter = jointAdapter
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
