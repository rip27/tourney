package com.example.tourney.page

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tourney.R
import com.example.tourney.data.Pref
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.setting.*

class Setting : AppCompatActivity() {

    lateinit var fAuth : FirebaseAuth
    lateinit var pref: Pref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)
        setSupportActionBar(toolbarSetting)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        pref = Pref(this)
        fAuth = FirebaseAuth.getInstance()

        editProfileonSetting.setOnClickListener {
            startActivity(Intent(this@Setting, EditProfile::class.java))
        }

        changePassonSetting.setOnClickListener {
            startActivity(Intent(this@Setting, ChangePassword::class.java))
        }

        logoutonSetting.setOnClickListener {
            fAuth.signOut()
            pref.setStatus(false)
            startActivity(Intent(this@Setting, Splash::class.java))
            finish()
        }
    }
}
