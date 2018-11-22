package com.erthru.myroom.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.erthru.myroom.R
import com.erthru.myroom.api.ApiConfig
import com.erthru.myroom.api.ApiConfig.Companion.IMG_URL
import com.erthru.myroom.api.model.LoadChatRoomResponse
import com.erthru.myroom.api.model.UserDetailResponse
import com.erthru.myroom.ui.login.LoginActivity
import com.erthru.myroom.ui.searchuser.SearchUserActivity
import com.erthru.myroom.ui.user.UserActivity
import com.erthru.myroom.utils.FCMInstanceID
import com.erthru.myroom.utils.GoogleLogin
import com.erthru.myroom.utils.SharedPrefUser
import com.erthru.myroom.utils.UIHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(receiver, IntentFilter("CHAT"))

        rvMain.layoutManager = LinearLayoutManager(this)
        rvMain.setHasFixedSize(true)

        fabChatMain.setOnClickListener { startActivity(Intent(this,SearchUserActivity::class.java)) }

        imgUserMain.setOnClickListener { startActivity(Intent(this,UserActivity::class.java)) }

        FCMInstanceID(this).saveToken(FirebaseInstanceId.getInstance().getToken())


    }

    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            refreshRoom()
        }

    }

    override fun onResume() {
        super.onResume()
        rvMain.adapter = null
        if(SharedPrefUser(this).getEmail() != null) {
            loadRoom()
            Glide.with(this@MainActivity).load(IMG_URL+SharedPrefUser(this@MainActivity).getPhoto()).into(imgUserMain)
        }
        loadUserData()
    }

    private fun loadRoom(){

        pbMain.visibility = View.VISIBLE
        tvEmptyMain.visibility = View.GONE

        ApiConfig().retrofit().loadChatRoom(SharedPrefUser(this).getEmail())
            .enqueue(object : retrofit2.Callback<LoadChatRoomResponse>{

                override fun onFailure(call: Call<LoadChatRoomResponse>, t: Throwable) {
                    Log.d("ONFAILURE",t.toString())
                    UIHandler(this@MainActivity).showErrorToast("Failed to load chat_primary")
                    pbMain.visibility = View.GONE
                }

                override fun onResponse(call: Call<LoadChatRoomResponse>, response: Response<LoadChatRoomResponse>) {

                    pbMain.visibility = View.GONE

                    if(response?.isSuccessful){

                        Log.d("CHAT_ROOM_SIZE",response?.body()?.result?.size.toString())
                        if(response?.body()?.result?.size != 0){
                            pbMain.visibility = View.GONE
                            val adapter = MainRecyclerView(this@MainActivity,response?.body()?.result)
                            adapter.notifyDataSetChanged()
                            rvMain.adapter = adapter
                        }else{
                            pbMain.visibility = View.GONE
                            tvEmptyMain.visibility = View.VISIBLE
                        }

                    }

                }

            })

    }

    private fun refreshRoom(){
        ApiConfig().retrofit().loadChatRoom(SharedPrefUser(this).getEmail())
            .enqueue(object : retrofit2.Callback<LoadChatRoomResponse>{

                override fun onFailure(call: Call<LoadChatRoomResponse>, t: Throwable) {
                    Log.d("ONFAILURE",t.toString())
                }

                override fun onResponse(call: Call<LoadChatRoomResponse>, response: Response<LoadChatRoomResponse>) {

                    pbMain.visibility = View.GONE

                    if(response?.isSuccessful){

                        Log.d("CHAT_ROOM_SIZE",response?.body()?.result?.size.toString())
                        if(response?.body()?.result?.size != 0){
                            val adapter = MainRecyclerView(this@MainActivity,response?.body()?.result)
                            adapter.notifyDataSetChanged()
                            rvMain.adapter = adapter
                        }else{
                            pbMain.visibility = View.GONE
                            tvEmptyMain.visibility = View.VISIBLE
                        }

                    }

                }

            })
    }

    private fun loadUserData(){

        pbMain.visibility = View.VISIBLE
        if(SharedPrefUser(this).getEmail() == null){

            ApiConfig().retrofit().userDetail(GoogleSignIn.getLastSignedInAccount(this)?.email)
                .enqueue(object : retrofit2.Callback<UserDetailResponse>{

                    override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                        Log.d("ONFAILURE",t.toString())
                        UIHandler(this@MainActivity).showErrorToast("Failed to load chat_primary")
                        pbMain.visibility = View.GONE
                    }

                    override fun onResponse(call: Call<UserDetailResponse>, response: Response<UserDetailResponse>) {

                        pbMain.visibility = View.GONE
                        SharedPrefUser(this@MainActivity).saveData(
                            response?.body()?.user?.user_email,
                            response?.body()?.user?.user_name,
                            response?.body()?.user?.user_photo
                        )

                        Glide.with(this@MainActivity).load(IMG_URL+SharedPrefUser(this@MainActivity).getPhoto()).into(imgUserMain)
                        loadRoom()
                        FCMInstanceID(this@MainActivity).saveToken(FirebaseInstanceId.getInstance().getToken())

                    }

                })

        }

    }

}
