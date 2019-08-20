package com.example.tourney

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.login.*

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        daftarAkun.setOnClickListener {
            startActivity(
                Intent(this, Daftar::class.java)
            )
        }
    }
}
