package com.example.tourney.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tourney.R
import com.example.tourney.model.TournamentModel
import com.example.tourney.model.UserModel
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class TournamentProfileAdapter : RecyclerView.Adapter<TournamentProfileAdapter.TournamentViewHolder> {
    lateinit var mCtx: Context
    lateinit var itemTour: List<TournamentModel>

    lateinit var dbRef: DatabaseReference
    constructor()
    constructor(mCtx: Context, list: List<TournamentModel>) {
        this.mCtx = mCtx
        this.itemTour = list
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TournamentViewHolder {
        val view: View = LayoutInflater.from(p0.context)
            .inflate(R.layout.item_list_tournament_on_profile, p0, false)
        val tourViewHolder = TournamentViewHolder(view)
        return tourViewHolder
    }

    override fun getItemCount(): Int {
        return itemTour.size
    }

    override fun onBindViewHolder(p0: TournamentViewHolder, p1: Int) {
        val tourModel: TournamentModel = itemTour.get(p1)
        FirebaseDatabase.getInstance()
            .getReference("user/")
            .child(tourModel.iduser!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data2: DataSnapshot) {
                    val userData =
                        data2.getValue(UserModel::class.java)
                    tourModel.userModel = userData
                    Glide.with(mCtx).load(tourModel.userModel!!.profile)
                        .centerCrop()
                        .error(R.drawable.logo)
                        .into(p0.imageProfile)
                    p0.tv_name.text = tourModel.userModel!!.name
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.e("cok", p0.message)
                }
            })
        p0.tv_peserta.text = tourModel.peserta
        p0.tv_category.text = tourModel.kategori
        p0.tv_nameT.text = tourModel.nameT
        p0.tv_domisili.text = tourModel.domisili
        p0.tv_tersisa.text = tourModel.tersisa
        Glide.with(mCtx).load(tourModel.brosurT)
            .centerCrop()
            .error(R.drawable.logo)
            .into(p0.brosur)
        p0.btDelete.setOnClickListener {
            val builder = AlertDialog.Builder(mCtx)
            builder.setMessage("Delete Item")
            builder.setPositiveButton("No") { dialog, i ->
                dialog.dismiss()
            }
            builder.setNegativeButton("Yes") { dialog, i ->
                dbRef = FirebaseDatabase.getInstance()
                    .getReference("tournament")
                dbRef.child(tourModel.key!!).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(
                            mCtx,
                            "Delete Success",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    inner class TournamentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llProfile: LinearLayout
        var imageProfile: ImageView
        var tv_name: TextView
        var tv_nameT: TextView
        var brosur: ImageView
        var tv_category: TextView
        var tv_domisili: TextView
        var tv_peserta: TextView
        var tv_tersisa: TextView
        var update: Button
        var btDelete: CircleImageView

        init {
            llProfile = itemView.findViewById(R.id.llp)
            btDelete = itemView.findViewById(R.id.btDelete)
            imageProfile = itemView.findViewById(R.id.profilePanitiaOnP)
            tv_name = itemView.findViewById(R.id.namePanitiaonP)
            tv_nameT = itemView.findViewById(R.id.nameTonP)
            brosur = itemView.findViewById(R.id.brosurTonP)
            tv_category = itemView.findViewById(R.id.categoryTonP)
            tv_domisili = itemView.findViewById(R.id.domisiliTonP)
            tv_peserta = itemView.findViewById(R.id.pesertaTonP)
            tv_tersisa = itemView.findViewById(R.id.tersisaTonP)
            update = itemView.findViewById(R.id.updateSlotonP)
        }
    }

}