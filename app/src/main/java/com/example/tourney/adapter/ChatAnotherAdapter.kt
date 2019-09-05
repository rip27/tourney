package com.example.tourney.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tourney.R
import com.example.tourney.model.ChatModel
import kotlinx.android.synthetic.main.message_item.view.*


class ChatAnotherAdapter(val chat: ArrayList<ChatModel>, val itemClick: (ChatModel) -> Unit) :
    RecyclerView.Adapter<ChatAnotherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item_another, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForecast(chat[position])
    }

    override fun getItemCount() = chat.size

    class ViewHolder(view: View, val itemClick: (ChatModel) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindForecast(message: ChatModel) {
            with(message) {
                itemView.messageAdapterMessageItem.text = message.text
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }
}