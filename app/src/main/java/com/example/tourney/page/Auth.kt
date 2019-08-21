package com.example.tourney.page

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tourney.R
import com.example.tourney.data.Pref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.login.*

class Auth : AppCompatActivity() {

    lateinit var pref: Pref
    private lateinit var fAuth: FirebaseAuth
    lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        buttonLogin.setOnClickListener {

            val email = editTextEmailLogin.text.toString()
            val password = editTextPasswordLogin.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill All Data", Toast.LENGTH_SHORT).show()
            } else {
                pref.setStatus(true)
                fAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("user")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    val user = fAuth.currentUser
                                    if (user != null) {
                                        pref.saveUID(user.uid)
                                        startActivity(Intent(this@Auth, Dashboard::class.java))
                                    } else {
                                        Log.e("TAG_ERROR", "USER NOT FOUND")
                                    }
                                }

                            })
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Username atau Password salah!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
        daftarAkun.setOnClickListener {
            var dialog: AlertDialog
            val alertDialog = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.daftar, null)
            alertDialog.setView(view)
            alertDialog.setTitle("DAFTAR")
            alertDialog.setPositiveButton("YES") { dialog, i ->
                val name = view.findViewById<EditText>(R.id.editTextNameRegister).text.toString()
                val phone = view.findViewById<EditText>(R.id.editTextPhoneRegister).text.toString()
                val gender = view.findViewById<Spinner>(R.id.sp_gender).selectedItem.toString()
                val email = view.findViewById<EditText>(R.id.editTextEmailRegister).text.toString()
                val password = view.findViewById<EditText>(R.id.editTextPasswordRegister).text.toString()
                if (name.isEmpty() || phone.isEmpty() || gender.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Fill All Data", Toast.LENGTH_SHORT).show()
                } else {
                    addUserToFirebase(name, phone, gender, email, password)
                }
            }
            alertDialog.setNegativeButton("NO") { dialog, i ->
                dialog.dismiss()
            }
            dialog = alertDialog.create()
            dialog.show()
        }
        pref = Pref(this)
        fAuth = FirebaseAuth.getInstance()
        if (!pref.cekStatus()!!) {

        } else {
            startActivity(
                Intent(
                    this,
                    Dashboard::class.java
                )
            )
            finish()
        }
    }

    private fun addUserToFirebase(name: String, phone: String, gender: String, email: String, password: String) {
        val uid = fAuth.currentUser?.uid
        fAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                dbRef = FirebaseDatabase.getInstance().getReference("user/$uid")
                dbRef.child("/id").setValue(uid)
                dbRef.child("/name").setValue(name)
                dbRef.child("/phone").setValue(phone)
                dbRef.child("/gender").setValue(gender)
                dbRef.child("/email").setValue(email)
                dbRef.child("/password").setValue(password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Sukses Daftar", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal Daftar", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal Daftar", Toast.LENGTH_SHORT).show()
            }
    }
}
