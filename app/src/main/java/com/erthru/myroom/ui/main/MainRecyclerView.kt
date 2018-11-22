package com.erthru.myroom.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.erthru.myroom.R
import com.erthru.myroom.api.ApiConfig.Companion.IMG_URL
import com.erthru.myroom.api.model.LoadChatRoomResult
import com.erthru.myroom.ui.chat.ChatActivity
import com.erthru.myroom.utils.SharedPrefUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.list_main.view.*

class MainRecyclerView(val context: Context, val data:ArrayList<LoadChatRoomResult>?) : RecyclerView.Adapter<MainRecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_main,parent,false))
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val room = data?.get(position)
        holder.v.tvNameLM.text = (room?.chat_sender_name+room?.chat_receiver_name)?.replace(SharedPrefUser(context)?.getName()!!,"")
        holder.v.tvLastChatLM.text = room?.chat_body
        Glide.with(context).load(IMG_URL+(room?.chat_sender_photo+room?.chat_receiver_photo).replace(SharedPrefUser(context).getPhoto()!!,"")).into(holder.v.imgLM)

        if(!room?.chat_unread.equals("0") && room?.chat_unread != null){
            holder.v.layouUnread.visibility = View.VISIBLE
            holder.v.tvUnreadLM.text = room?.chat_unread
            holder.v.tvLastChatLM.setTypeface(holder.v.tvLastChatLM.typeface, Typeface.BOLD)
        }



        holder.v.setOnClickListener {
            val i = Intent(context,ChatActivity::class.java)
            i.putExtra("friendName",(room?.chat_sender_name+room?.chat_receiver_name)?.replace(SharedPrefUser(context)?.getName()!!,""))
            i.putExtra("friendEmail",(room?.a_chat_sender+room?.a_chat_receiver)?.replace(SharedPrefUser(context)?.getEmail()!!,""))
            i.putExtra("friendPhoto",(room?.chat_sender_photo+room?.chat_receiver_photo)?.replace(SharedPrefUser(context)?.getPhoto()!!,""))
            context.startActivity(i)
        }

    }

    class ViewHolder(val v:View) : RecyclerView.ViewHolder(v)

}