package com.example.tourney.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tourney.R
import com.example.tourney.data.Pref
import com.example.tourney.model.TournamentModel
import com.example.tourney.model.UserModel
import com.example.tourney.page.DetailTournament
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class TournamentAdapter : RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder> {
    lateinit var mCtx: Context
    lateinit var itemTournament: List<TournamentModel>
    lateinit var pref: Pref
    lateinit var dbRef: DatabaseReference
    lateinit var fauth: FirebaseAuth

    constructor()
    constructor(mCtx: Context, list: List<TournamentModel>) {
        this.mCtx = mCtx
        this.itemTournament = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TournamentViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_tournament, parent, false)
        val bukuViewHolder = TournamentViewHolder(view)
        return bukuViewHolder
    }

    override fun getItemCount(): Int {
        return itemTournament.size
    }

    override fun onBindViewHolder(holder: TournamentViewHolder, position: Int) {
        val tournamentModel: TournamentModel = itemTournament.get(position)
        fauth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance()
            .getReference("user/")
            .child(tournamentModel.iduser!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data2: DataSnapshot) {
                    val userData =
                        data2.getValue(UserModel::class.java)
                    tournamentModel.userModel = userData
                    Glide.with(mCtx).load(tournamentModel.userModel!!.profile)
                        .centerCrop()
                        .error(R.mipmap.ic_launcher)
                        .into(holder.image)
                    holder.namePanitia.text = tournamentModel.userModel!!.name
                }

                override fun onCancelled(holder: DatabaseError) {
                    Log.e("cok", holder.message)
                }
            })
        Glide.with(mCtx).load(tournamentModel.brosurT)
            .centerCrop()
            .error(R.mipmap.ic_launcher)
            .into(holder.brosurT)
        holder.nameT.text = tournamentModel.nameT
        holder.categoryT.text = tournamentModel.kategori
        holder.domisiliT.text = tournamentModel.domisili
        holder.pesertaT.text = tournamentModel.peserta
        holder.tersisaT.text = tournamentModel.tersisa
        holder.save.setOnClickListener {
            holder.save.visibility = View.GONE
            holder.check.visibility = View.VISIBLE
        }
        holder.check.setOnClickListener {
            Toast.makeText(mCtx, "Sudah Disimpan!", Toast.LENGTH_SHORT).show()
        }
        holder.ll.setOnClickListener {
            Toast.makeText(mCtx, "Detail", Toast.LENGTH_SHORT).show()
            val intent = Intent(mCtx, DetailTournament::class.java)
            intent.putExtra("nama_user", tournamentModel.userModel!!.name)
            intent.putExtra("foto_profile", tournamentModel.userModel!!.profile)
            intent.putExtra("iduser", tournamentModel.iduser)
            intent.putExtra("id_tournament", tournamentModel.id_tournament)
            intent.putExtra("nameT", tournamentModel.nameT)
            intent.putExtra("brosur", tournamentModel.brosurT)
            intent.putExtra("category", tournamentModel.kategori)
            intent.putExtra("domisili", tournamentModel.domisili)
            intent.putExtra("peserta", tournamentModel.peserta)
            intent.putExtra("slot", tournamentModel.slot)
            intent.putExtra("tersisa", tournamentModel.tersisa)
            intent.putExtra("dibuka", tournamentModel.dibuka)
            intent.putExtra("ditutup", tournamentModel.ditutup)
            mCtx.startActivity(intent)
        }
        if (fauth.currentUser!!.uid == tournamentModel.iduser){
            holder.update.visibility = View.VISIBLE
            holder.ikut.visibility = View.GONE
        }else{
            holder.update.visibility = View.GONE
            holder.ikut.visibility = View.VISIBLE
        }
    }

    inner class TournamentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ll: LinearLayout
        var image: CircleImageView
        var nameT: TextView
        var namePanitia: TextView
        var brosurT: ImageView
        var categoryT: TextView
        var domisiliT: TextView
        var pesertaT: TextView
        var tersisaT: TextView
        var save: CircleImageView
        var check: CircleImageView
        var ikut: Button
        var update: Button

        init {
            ll = itemView.findViewById(R.id.ll)
            image = itemView.findViewById(R.id.profilePanitia)
            nameT = itemView.findViewById(R.id.nameT)
            namePanitia = itemView.findViewById(R.id.namePanitia)
            brosurT = itemView.findViewById(R.id.brosurT)
            categoryT = itemView.findViewById(R.id.categoryT)
            domisiliT = itemView.findViewById(R.id.domisiliT)
            pesertaT = itemView.findViewById(R.id.pesertaT)
            tersisaT = itemView.findViewById(R.id.tersisaT)
            save = itemView.findViewById(R.id.saveT)
            check = itemView.findViewById(R.id.checkedT)
            ikut = itemView.findViewById(R.id.ikutTour)
            update = itemView.findViewById(R.id.updateSlot)
        }
    }
}