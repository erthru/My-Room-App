package com.erthru.myroom.ui.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.erthru.myroom.R
import com.erthru.myroom.api.ApiConfig
import com.erthru.myroom.api.ApiConfig.Companion.IMG_URL
import com.erthru.myroom.api.model.LoadChatResponse
import com.erthru.myroom.api.model.NewChatResponse
import com.erthru.myroom.api.model.ReadChatResponse
import com.erthru.myroom.utils.FCMInstanceID
import com.erthru.myroom.utils.SharedPrefUser
import com.erthru.myroom.utils.UIHandler
import kotlinx.android.synthetic.main.activity_chat.*
import retrofit2.Call
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    lateinit var i:Intent
    lateinit var friendName:String
    lateinit var friendEmail:String
    lateinit var friendPhoto:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        registerReceiver(receiver, IntentFilter("CHAT"))

        i = intent
        friendName = i.getStringExtra("friendName")
        friendEmail = i.getStringExtra("friendEmail")
        friendPhoto = i.getStringExtra("friendPhoto")

        Glide.with(this).load(IMG_URL+friendPhoto).into(imgChat)
        tvNameChat.text = friendName

        btnBackChat.setOnClickListener { this.finish() }

        rvChat.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        //layoutManager.stackFromEnd = true
        rvChat.layoutManager = layoutManager

        loadChat()

        btnChat.setOnClickListener { newChat() }

    }

    private val receiver = object : BroadcastReceiver(){

        override fun onReceive(p0: Context?, p1: Intent?) {
            refreshChat()
        }

    }

    private fun loadChat(){

        pbChat.visibility = View.VISIBLE

        ApiConfig().retrofit().loadChat(SharedPrefUser(this).getEmail(),friendEmail)
            .enqueue(object : retrofit2.Callback<LoadChatResponse>{
                override fun onFailure(call: Call<LoadChatResponse>, t: Throwable) {
                    Log.d("ONFAILURE",t.toString())
                    pbChat.visibility = View.GONE
                    UIHandler(this@ChatActivity).showErrorToast("Failed to load chat.")
                    finish()
                }

                override fun onResponse(call: Call<LoadChatResponse>, response: Response<LoadChatResponse>) {

                    if(response?.isSuccessful){
                        pbChat.visibility = View.GONE
                        val adapter = ChatRecyclerView(this@ChatActivity,response?.body()?.result)
                        adapter.notifyDataSetChanged()
                        rvChat.adapter = adapter
                        readChat()
                    }

                }

            })

    }

    private fun refreshChat(){

        ApiConfig().retrofit().loadChat(SharedPrefUser(this).getEmail(),friendEmail)
            .enqueue(object : retrofit2.Callback<LoadChatResponse>{
                override fun onFailure(call: Call<LoadChatResponse>, t: Throwable) {
                    Log.d("ONFAILURE",t.toString())
                }

                override fun onResponse(call: Call<LoadChatResponse>, response: Response<LoadChatResponse>) {

                    if(response?.isSuccessful){
                        val adapter = ChatRecyclerView(this@ChatActivity,response?.body()?.result)
                        adapter.notifyDataSetChanged()
                        rvChat.adapter = adapter
                        readChat()
                    }

                }

            })
    }

    private fun readChat(){

        ApiConfig().retrofit().readChat(friendEmail,SharedPrefUser(this).getEmail())
            .enqueue(object : retrofit2.Callback<ReadChatResponse>{
                override fun onFailure(call: Call<ReadChatResponse>, t: Throwable) {
                    Log.d("ONFAILURE",t.toString())
                }

                override fun onResponse(call: Call<ReadChatResponse>, response: Response<ReadChatResponse>) {
                    if(response?.isSuccessful){
                        if(response?.body()?.error == false){
                            Log.d("CHAT_READ","SUCCESS")
                        }
                    }
                }

            })

    }

    private fun newChat(){

        btnChat.visibility = View.GONE
        pb2Chat.visibility = View.VISIBLE

        ApiConfig().retrofit().newChat(
            txChat.text.toString(),
            SharedPrefUser(this).getEmail(),
            friendEmail
        )
            .enqueue(object : retrofit2.Callback<NewChatResponse>{
                override fun onFailure(call: Call<NewChatResponse>, t: Throwable) {
                    btnChat.visibility = View.VISIBLE
                    pb2Chat.visibility = View.GONE
                    Log.d("ONFAILURE",t.toString())
                    UIHandler(this@ChatActivity).showErrorToast("Failed to send message.")
                }

                override fun onResponse(call: Call<NewChatResponse>, response: Response<NewChatResponse>) {

                    if(response?.isSuccessful){

                        if(response?.body()?.error == false){
                            btnChat.visibility = View.VISIBLE
                            pb2Chat.visibility = View.GONE
                            refreshChat()
                            txChat.setText("")
                            FCMInstanceID(this@ChatActivity).sendNotificationPrepare(friendEmail,"New message from "+SharedPrefUser(this@ChatActivity).getName())
                        }else{
                            btnChat.visibility = View.VISIBLE
                            pb2Chat.visibility = View.GONE
                            UIHandler(this@ChatActivity).showErrorToast("Failed to send message.")
                        }

                    }

                }

            })

    }

}
