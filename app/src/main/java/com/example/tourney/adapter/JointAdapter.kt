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

class JointAdapter : RecyclerView.Adapter<JointAdapter.JointViewHolder> {
    lateinit var mCtx: Context
    lateinit var itemJoint: List<TournamentModel>
    lateinit var pref: Pref
    lateinit var dbRef: DatabaseReference
    lateinit var fauth: FirebaseAuth

    constructor()
    constructor(mCtx: Context, list: List<TournamentModel>) {
        this.mCtx = mCtx
        this.itemJoint = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JointViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_join_t, parent, false)
        val jointViewHolder = JointViewHolder(view)
        return jointViewHolder
    }

    override fun getItemCount(): Int {
        return itemJoint.size
    }

    override fun onBindViewHolder(holder: JointViewHolder, position: Int) {
        val tournamentModel: TournamentModel = itemJoint.get(position)
        fauth = FirebaseAuth.getInstance()
        holder.kat.text = tournamentModel.kategori
        holder.namat.text = tournamentModel.nameT
        holder.ll.setOnClickListener {
            val intent = Intent(mCtx, JoinMine::class.java)
            intent.putExtra("idtour", tournamentModel.id_tournament)
            mCtx.startActivity(intent)
        }
    }

    inner class JointViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ll: LinearLayout
        var namat: TextView
        var kat: TextView

        init {
            ll = itemView.findViewById(R.id.lljt)
            namat = itemView.findViewById(R.id.tv_namatt)
            kat = itemView.findViewById(R.id.tv_kate)
        }
    }
}