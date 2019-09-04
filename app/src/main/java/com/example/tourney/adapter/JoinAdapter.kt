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
import com.example.tourney.page.DetailJoin
import com.example.tourney.page.DetailTournament
import com.example.tourney.page.IkutTour
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class JoinAdapter : RecyclerView.Adapter<JoinAdapter.JoinViewHolder> {
    lateinit var mCtx: Context
    lateinit var itemJoin: List<JoinModel>
    lateinit var pref: Pref
    lateinit var dbRef: DatabaseReference
    lateinit var fauth: FirebaseAuth

    constructor()
    constructor(mCtx: Context, list: List<JoinModel>) {
        this.mCtx = mCtx
        this.itemJoin = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_join, parent, false)
        val joinViewHolder = JoinViewHolder(view)
        return joinViewHolder
    }

    override fun getItemCount(): Int {
        return itemJoin.size
    }

    override fun onBindViewHolder(holder: JoinViewHolder, position: Int) {
        val joinModel: JoinModel = itemJoin.get(position)
        fauth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance()
            .getReference("user/")
            .child(joinModel.iduser2!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data2: DataSnapshot) {
                    val userData =
                        data2.getValue(UserModel::class.java)
                    joinModel.user2Model = userData
                    holder.pani.text = joinModel.user2Model!!.name
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
        if (joinModel.status!! == "PENDING"){

        }else if (joinModel.status!! == "APPROVED"){
            holder.sta.visibility = View.VISIBLE
            holder.stp.visibility = View.GONE
            holder.str.visibility = View.GONE
        }else{
            holder.sta.visibility = View.GONE
            holder.stp.visibility = View.GONE
            holder.str.visibility = View.VISIBLE
        }
        holder.ll.setOnClickListener {
            val intent = Intent(mCtx, DetailJoin::class.java)
            intent.putExtra("buktitf" , joinModel.buktitf)
            intent.putExtra("status" , joinModel.status)
            intent.putExtra("iduser1" , joinModel.iduser1)
            intent.putExtra("iduser2" , joinModel.iduser2)
            intent.putExtra("idtour" , joinModel.idtour)
            mCtx.startActivity(intent)
        }
    }

    inner class JoinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ll: LinearLayout
        var namat: TextView
        var bukti: ImageView
        var pani: TextView
        var kat: TextView
        var stp: TextView
        var sta: TextView
        var str: TextView

        init {
            ll = itemView.findViewById(R.id.llj)
            bukti = itemView.findViewById(R.id.image_bukti_tf)
            namat = itemView.findViewById(R.id.tv_namat)
            pani = itemView.findViewById(R.id.tv_penyelenggara)
            kat = itemView.findViewById(R.id.tv_kategori)
            stp = itemView.findViewById(R.id.tv_statuspending)
            sta = itemView.findViewById(R.id.tv_statusapproved)
            str = itemView.findViewById(R.id.tv_statusrejected)
        }
    }
}