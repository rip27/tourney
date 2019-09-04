package com.example.tourney.page

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.tourney.utils.ApiClient
import com.bumptech.glide.Glide
import com.example.tourney.R
import com.example.tourney.adapter.AreaAdapter
import com.example.tourney.data.Pref
import com.example.tourney.model.AreaModel
import com.example.tourney.model.ProvinsiModel
import com.example.tourney.utils.ApiInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.tambah_tour.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class TambahTour : AppCompatActivity() {

    val REQUEST_CODE_IMAGE = 10002
    val PERMISSION_RC = 10003
    lateinit var filePathImage: Uri
    var value = 0.0
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var pref: Pref
    lateinit var dbRef: DatabaseReference
    lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tambah_tour)
        setSupportActionBar(toolbarTambahTour)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        fAuth = FirebaseAuth.getInstance()
        pref = Pref(this)
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference
        contentDomisili.setOnClickListener {
            showDialogArea()
        }
        imageBrosur.setOnClickListener {
            when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ), PERMISSION_RC
                        )
                    } else {
                        imageChooser()
                    }
                }
                else -> {
                    imageChooser()
                }
            }
        }
        upload_tour.setOnClickListener {
            val nameT = et_nameT.text.toString()
            val domisili = textDomisili.text.toString()
            val system = system.selectedItem.toString()
            val arena = arena.text.toString()
            val slot = slot.text.toString()
            val kategori = kategori.text.toString()
            val peserta = peserta.selectedItem.toString()
            val dibuka = dibuka.text.toString()
            val ditutup = ditutup.text.toString()
            val harga = price.text.toString()

            if (nameT.isNotEmpty() || domisili.isNotEmpty() ||
                system.isNotEmpty()|| arena.isNotEmpty() ||
                slot.isNotEmpty()|| peserta.isNotEmpty() ||
                dibuka.isNotEmpty() || ditutup.isNotEmpty() ||
                kategori.isNotEmpty() || harga.isNotEmpty()
            ) {
                addTourToFirebase(nameT, domisili, system, arena, slot, peserta, dibuka, ditutup, kategori, harga)
            } else {
                Toast.makeText(
                    this,
                    "Fill Data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun addTourToFirebase(
        nameT: String,
        domisili: String,
        system: String,
        arena: String,
        slot: String,
        peserta: String,
        dibuka: String,
        ditutup: String,
        kategori: String,
        harga: String
    ) {
        val nameXXX = UUID.randomUUID().toString()
        val idtour = UUID.randomUUID().toString()
        val uid = fAuth.currentUser?.uid
        val storageRef: StorageReference = storageReference
            .child("images/$uid/$nameXXX.${GetFileExtension(filePathImage)}")
        storageRef.putFile(filePathImage).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                dbRef = FirebaseDatabase.getInstance().getReference("tournament/$idtour")
                dbRef.child("brosurT").setValue(it.toString())
                dbRef.child("id_tournament").setValue(idtour)
                dbRef.child("nameT").setValue(nameT)
                dbRef.child("domisili").setValue(domisili)
                dbRef.child("system").setValue(system)
                dbRef.child("arena").setValue(arena)
                dbRef.child("slot").setValue(slot)
                dbRef.child("kategori").setValue(kategori)
                dbRef.child("peserta").setValue(peserta)
                dbRef.child("tersisa").setValue(slot)
                dbRef.child("dibuka").setValue(dibuka)
                dbRef.child("ditutup").setValue(ditutup)
                dbRef.child("price").setValue(harga)
                FirebaseDatabase.getInstance().getReference("user/")
                    .child("${fAuth.uid}/id")
                    .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                dbRef.child("iduser").setValue(p0.value)
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                Log.e("Error", p0.message)
                            }

                        })
            }
            Toast.makeText(
                this,
                "Success Upload",
                Toast.LENGTH_SHORT
            ).show()
            progressDownload.visibility = View.GONE
            upload_tour.visibility = View.VISIBLE
        }.addOnFailureListener {
            Log.e("TAG_ERROR", it.message)
        }.addOnProgressListener { taskSnapshot ->
            value = (100.0 * taskSnapshot
                .bytesTransferred / taskSnapshot.totalByteCount)
            upload_tour.visibility = View.GONE
            progressDownload.visibility = View.VISIBLE
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_RC -> {
                if (grantResults.isEmpty() ||
                    grantResults[0] == PackageManager.PERMISSION_DENIED
                ) {
                    Toast.makeText(
                        this,
                        "Ditolak",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    imageChooser()
                }
            }
        }
    }
    private fun imageChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Image"),
            REQUEST_CODE_IMAGE
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_CODE_IMAGE -> {
                filePathImage = data?.data!!
                try {
                    val bitmap: Bitmap = MediaStore
                        .Images.Media.getBitmap(
                        this.contentResolver, filePathImage
                    )
                    Glide.with(this).load(bitmap)
                        .override(250, 250)
                        .centerCrop().into(imageBrosur)
                } catch (x: IOException) {
                    x.printStackTrace()
                }
            }
        }
    }

    fun GetFileExtension(uri: Uri): String? {
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    fun showDialogArea(){
        var dialog : AlertDialog
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_area, null)

        builder.setView(view)
        val searchArea : EditText = view.findViewById(R.id.searchArea)
        val listArea : ListView = view.findViewById(R.id.listArea)
        getArea(listArea)
        searchArea.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    getArea(listArea, s.toString())
                } else {
                    getArea(listArea)
                }
            }

        })
        dialog = builder.create()
        dialog.show()
        listArea.setOnItemClickListener { parent, view, position, id ->
            val area = parent.adapter.getItem(position) as ProvinsiModel
            textDomisili.text = area.nama
            dialog.dismiss()
        }

    }

    fun getArea(listArea : ListView) {
        val apiInterface = ApiClient().getClient().create(ApiInterface::class.java)
        apiInterface.getArea().enqueue(object : Callback<AreaModel> {
            override fun onResponse(call: Call<AreaModel>, response: Response<AreaModel>) {
                if (response.code() == 200) {
                    listArea.adapter = AreaAdapter(response.body()!!.semuaprovinsi!!,this@TambahTour)
                    Toast.makeText(this@TambahTour, "Gk Error cok", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@TambahTour, "Gagal res w/out search", Toast.LENGTH_SHORT).show()
                    Log.e("without search", "error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AreaModel>, t: Throwable) {
                Toast.makeText(this@TambahTour, "Gagal wt s fail", Toast.LENGTH_SHORT).show()
                Log.e("without search", "ERROR ${t.message}", t)
            }

        })
    }

    fun getArea(listArea: ListView, nama : String) {
        val apiInterface = ApiClient().getClient().create(ApiInterface::class.java)
        apiInterface.getArea(nama).enqueue(object : Callback<AreaModel> {
            override fun onResponse(call: Call<AreaModel>, response: Response<AreaModel>) {
                if (response.code() == 200) {
                    listArea.adapter = AreaAdapter(response.body()!!.semuaprovinsi!!,this@TambahTour)
                } else {
                    Toast.makeText(this@TambahTour, "Gagal w s", Toast.LENGTH_SHORT).show()
                    Log.e("with search", "error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AreaModel>, t: Throwable) {
                Toast.makeText(this@TambahTour, "Gagal w s f", Toast.LENGTH_SHORT).show()
                Log.e("with search", "${t.message}")
            }

        })
    }
}
