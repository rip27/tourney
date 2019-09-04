package com.example.tourney.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tourney.R
import com.example.tourney.data.Pref
import com.example.tourney.model.JoinModel
import com.example.tourney.model.TournamentModel
import com.example.tourney.model.UserModel
import com.example.tourney.page.DetailTournament
import com.example.tourney.page.IkutTour
import com.example.tourney.page.JoinMine
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class JoinmAdapter : RecyclerView.Adapter<JoinmAdapter.JoinmViewHolder> {
    lateinit var mCtx: Context
    lateinit var itemJoinm: List<JoinModel>
    lateinit var pref: Pref
    lateinit var dbRef: DatabaseReference
    lateinit var fauth: FirebaseAuth

    constructor()
    constructor(mCtx: Context, list: List<JoinModel>) {
        this.mCtx = mCtx
        this.itemJoinm = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinmViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_join_m, parent, false)
        val joinmViewHolder = JoinmViewHolder(view)
        return joinmViewHolder
    }

    override fun getItemCount(): Int {
        return itemJoinm.size
    }

    override fun onBindViewHolder(holder: JoinmViewHolder, position: Int) {
        val joinModel: JoinModel = itemJoinm.get(position)
        fauth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance()
            .getReference("user/")
            .child(joinModel.iduser1!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data2: DataSnapshot) {
                    val userData =
                        data2.getValue(UserModel::class.java)
                    joinModel.user1Model = userData
                    holder.pendaftar.text = joinModel.user1Model!!.name
                }

                override fun onCancelled(holder: DatabaseError) {
                    Log.e("cok", holder.message)
                }
            })
        FirebaseDatabase.getInstance()
            .getReference("tournament/")
            .child(joinModel.idtour!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data2: DataSnapshot) {
                    val tourData =
                        data2.getValue(TournamentModel::class.java)
                    joinModel.tourModel = tourData
                    holder.namat.text = joinModel.tourModel!!.nameT
                    holder.kat.text = joinModel.tourModel!!.kategori
                }

                override fun onCancelled(holder: DatabaseError) {
                    Log.e("cok", holder.message)
                }
            })
        Glide.with(mCtx).load(joinModel.buktitf).into(holder.bukti)
        if (joinModel.status!! == "APPROVED"){
            holder.ap.visibility = View.GONE
            holder.rj.visibility = View.GONE
            holder.tap.visibility = View.VISIBLE
        }else if (joinModel.status!! == "REJECTED"){
            holder.trj.visibility = View.VISIBLE
            holder.ap.visibility = View.GONE
            holder.rj.visibility = View.GONE
        }
        holder.ap.setOnClickListener {
            dbRef = FirebaseDatabase.getInstance().reference
            dbRef.child("join").child(joinModel.idtour!!).child(joinModel.idjoin!!).child("status").setValue("APPROVED")
            val newSlot = joinModel.tourModel!!.tersisa!!.toInt() - 1
            dbRef.child("tournament").child(joinModel.idtour!!).child("tersisa").setValue(newSlot.toString())

        }
        holder.rj.setOnClickListener {
            dbRef = FirebaseDatabase.getInstance().getReference("join")
            dbRef.child(joinModel.idtour!!).child(joinModel.idjoin!!).child("status").setValue("REJECTED")
        }
    }

    inner class JoinmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ll: LinearLayout
        var namat: TextView
        var pendaftar: TextView
        var tap: TextView
        var trj: TextView
        var kat: TextView
        var bukti: ImageView
        var ap: Button
        var rj: Button

        init {
            ll = itemView.findViewById(R.id.lljm)
            namat = itemView.findViewById(R.id.tv_namat_mine)
            pendaftar = itemView.findViewById(R.id.tv_pendaftar)
            kat = itemView.findViewById(R.id.tv_kategori_mine)
            bukti = itemView.findViewById(R.id.image_bukti_tf_mine)
            ap = itemView.findViewById(R.id.btApproved)
            rj = itemView.findViewById(R.id.btRejected)
            tap = itemView.findViewById(R.id.tv_approved)
            trj = itemView.findViewById(R.id.tv_rejected)
        }
    }
}