package com.example.tourney.page

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import com.example.tourney.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.change_password.*
import kotlinx.android.synthetic.main.edit_profile.*
import kotlinx.android.synthetic.main.login.*

class ChangePassword : AppCompatActivity() {


    lateinit var fAuth: FirebaseAuth
    lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)
        setSupportActionBar(toolbarChangePass)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        fAuth = FirebaseAuth.getInstance()
        hideShowLastPass.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                lastPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                lastPass.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        hideShowNewPass.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                newPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                newPass.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        val userid = fAuth.currentUser?.uid
        val dataUserRef = FirebaseDatabase.getInstance().getReference("user/$userid")

        dataUserRef.child("password")
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                passss.text = p0.value.toString()
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
        buttonSavePass.setOnClickListener {
            val pas = passss.text.toString()
            val lp = lastPass.text.toString()
            val np = newPass.text.toString()
            if (lp.isNotEmpty() || np.isNotEmpty()){
                if (pas.equals(lp)) {
                    dbRef = FirebaseDatabase.getInstance().getReference("user/$userid").child("password")
                    dbRef.setValue(np)
                    Toast.makeText(this@ChangePassword, "Sukses", Toast.LENGTH_SHORT).show()
                    Handler().postDelayed({
                        onBackPressed()
                    }, 1000)
                } else {
                    Toast.makeText(this@ChangePassword, "Insert Correct Password $pas $lp $np", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this@ChangePassword, "Fill All Data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
