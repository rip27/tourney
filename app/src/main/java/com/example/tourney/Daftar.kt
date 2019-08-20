package com.example.tourney

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.daftar.*

class Daftar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.daftar)
        loginAkun.setOnClickListener {
            startActivity(
                Intent(this, Login::class.java)
            )
            finish()
        }
    }
}
