package com.example.tourney.page

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tourney.R
import com.example.tourney.data.Pref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.login.*

class Auth : AppCompatActivity() {

    lateinit var pref: Pref
    lateinit var fAuth: FirebaseAuth
    lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        pref = Pref(this)
        fAuth = FirebaseAuth.getInstance()
        hideShowPass.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                editTextPasswordLogin.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                editTextPasswordLogin.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        buttonLogin.setOnClickListener {
            buttonLogin.visibility = View.GONE
            progressLogin.visibility = View.VISIBLE
            val email = editTextEmailLogin.text.toString()
            val password = editTextPasswordLogin.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill All Data", Toast.LENGTH_SHORT).show()
                buttonLogin.visibility = View.VISIBLE
                progressLogin.visibility = View.GONE
            } else {
                fAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        pref.setStatus(true)
                        FirebaseDatabase.getInstance().getReference("user")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    val user = fAuth.currentUser
                                    updateUI(user)
                                }

                            })
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Username atau Password salah!",
                            Toast.LENGTH_SHORT
                        ).show()
                        buttonLogin.visibility = View.VISIBLE
                        progressLogin.visibility = View.GONE
                    }
            }
        }
        daftarAkun.setOnClickListener {

            var dialog: AlertDialog
            val alertDialog = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.daftar, null)
            alertDialog.setView(view)
            alertDialog.setTitle("DAFTAR")
            alertDialog.setPositiveButton("DAFTAR") { dialog, i ->
                val hideShowPassRegister = view.findViewById<CheckBox>(R.id.hideShowPassRegister)
                val etpassreg = view.findViewById<EditText>(R.id.editTextPasswordRegister)
                hideShowPassRegister.setOnCheckedChangeListener { compoundButton, b ->
                    if (compoundButton.isChecked) {
                        etpassreg.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    } else {
                        etpassreg.transformationMethod = PasswordTransformationMethod.getInstance()
                    }
                }
                val name = view.findViewById<EditText>(R.id.editTextNameRegister).text.toString()
                val phone = view.findViewById<EditText>(R.id.editTextPhoneRegister).text.toString()
                val gender = view.findViewById<Spinner>(R.id.sp_gender).selectedItem.toString()
                val email = view.findViewById<EditText>(R.id.editTextEmailRegister).text.toString()
                val password = view.findViewById<EditText>(R.id.editTextPasswordRegister).text.toString()
                if (name.isEmpty() || phone.isEmpty() || gender.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Fill All Data", Toast.LENGTH_SHORT).show()
                } else {
                    fAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                addUserToFirebase(name, phone, gender, email, password)
                                Toast.makeText(this, "Register Berhasil!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "GAGAL", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            alertDialog.setNegativeButton("NO") { dialog, i ->
                dialog.dismiss()
            }
            dialog = alertDialog.create()
            dialog.show()
        }
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
        dbRef = FirebaseDatabase.getInstance().getReference("user/$uid")
        dbRef.child("/id").setValue(uid)
        dbRef.child("/name").setValue(name)
        dbRef.child("/phone").setValue(phone)
        dbRef.child("/gender").setValue(gender)
        dbRef.child("/email").setValue(email)
        dbRef.child("/password").setValue(password)
    }

    //
//    override fun onStart() {
//        super.onStart()
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null) {
//            updateUI(user)
//            finish()
//        }
//    }
//
    fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            pref.saveUID(user.uid)
            startActivity(Intent(this, Dashboard::class.java))
        } else {
            Log.e("TAG_ERROR", "user tidak ada")
        }
    }
}
