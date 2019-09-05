package com.example.tourney.page

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourney.R
import com.example.tourney.adapter.ChatAdapter
import com.example.tourney.model.ChatModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.chat.*

class Chat : AppCompatActivity() {

    var dbRef: DatabaseReference? = null
    lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat)

        fAuth = FirebaseAuth.getInstance()
        textnamechat.text = intent.getStringExtra("namatujuan")

        chatback.setOnClickListener {
            onBackPressed()
        }

        initFirebase()

        setupSendButton()

        createFirebaseListener()

    }

    private fun setupSendButton() {
        mainActivitySendButton.setOnClickListener {
            if (!mainActivityEditText.text.toString().isEmpty()) {
                sendData()
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initFirebase() {
        //init firebase
        FirebaseApp.initializeApp(applicationContext)

//        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)

        //get reference to our db
        dbRef = FirebaseDatabase.getInstance().reference
    }

    private fun createFirebaseListener() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val toReturn: ArrayList<ChatModel> = ArrayList()

                for (data in dataSnapshot.children) {
                    val messageData = data.getValue(ChatModel::class.java)

                    //unwrap
                    val message = messageData?.let { it } ?: continue

                    toReturn.add(message)
                }

                //sort so newest at bottom
                toReturn.sortBy { message ->
                    message.timestamp
                }

                setupAdapter(toReturn)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //log error
            }
        }
        dbRef?.child("messages")?.orderByChild("fromid")?.equalTo(fAuth.currentUser?.uid)?.addValueEventListener(postListener)
    }


    private fun sendData() {
//        dbRef?.child("messages")?.child(java.lang.String.valueOf(System.currentTimeMillis()))
//            ?.setValue(ChatModel(mainActivityEditText.text.toString()))
        dbRef?.child("messages")?.child(java.lang.String.valueOf(System.currentTimeMillis()))
            ?.setValue(ChatModel(mainActivityEditText.text.toString(), intent.getStringExtra("iduser"), fAuth.currentUser?.uid))

        //clear the text
        mainActivityEditText.setText("")
    }

    private fun setupAdapter(data: ArrayList<ChatModel>) {
        val linearLayoutManager = LinearLayoutManager(this)
        rvChat.layoutManager = linearLayoutManager
        rvChat.adapter = ChatAdapter(data) {
            Toast.makeText(this, "${it.text} clicked", Toast.LENGTH_SHORT).show()
        }

        //scroll to bottom
        rvChat.scrollToPosition(data.size - 1)
    }

}