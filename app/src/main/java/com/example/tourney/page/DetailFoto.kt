package com.example.tourney.page

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.tourney.R
import kotlinx.android.synthetic.main.detail_foto.*

class DetailFoto : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_foto)
        back.setOnClickListener {
            onBackPressed()
        }
        val foto = intent.getStringExtra("foto")
        Glide.with(this@DetailFoto)
            .load(foto).error(R.drawable.belum)
            .into(fotofoto)
    }
}
