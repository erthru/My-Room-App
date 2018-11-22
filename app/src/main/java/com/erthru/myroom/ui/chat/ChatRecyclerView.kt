package com.erthru.myroom.ui.chat

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.erthru.myroom.R
import com.erthru.myroom.api.model.LoadChatResult
import com.erthru.myroom.utils.SharedPrefUser
import com.erthru.myroom.utils.UIHandler
import kotlinx.android.synthetic.main.list_chat.view.*

class ChatRecyclerView(val context: Context, val data:ArrayList<LoadChatResult>?) : RecyclerView.Adapter<ChatRecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_chat,parent,false))
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat = data?.get(position)

        if(chat?.chat_sender?.equals(SharedPrefUser(context).getEmail())!!){
            holder.v.tvPrimaryLC.visibility = View.VISIBLE
            holder.v.tvPrimaryLC.text = chat?.chat_body
            holder.v.tvAccentLC.visibility = View.GONE
        }else{
            holder.v.tvPrimaryLC.visibility = View.GONE
            holder.v.tvAccentLC.visibility = View.VISIBLE
            holder.v.tvAccentLC.text = chat?.chat_body
        }

    }


    class ViewHolder(val v: View) : RecyclerView.ViewHolder(v)

}