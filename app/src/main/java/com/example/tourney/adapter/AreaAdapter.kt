package com.example.tourney.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.tourney.R
import com.example.tourney.model.AreaModel
import com.example.tourney.model.ProvinsiModel

class AreaAdapter(val areas: ArrayList<ProvinsiModel>, val context: Context) : BaseAdapter() {


    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val area = areas.get(position)
        val view = LayoutInflater.from(context).
                inflate(R.layout.item_area, parent, false)

        val txtArea : TextView = view.findViewById(R.id.txtArea)

        txtArea.text = area.nama
        return view
    }

    override fun getItem(position: Int): Any {
        return areas.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return areas.size
    }
}