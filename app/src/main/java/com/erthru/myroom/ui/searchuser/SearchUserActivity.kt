package com.erthru.myroom.ui.searchuser

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.erthru.myroom.R
import com.erthru.myroom.api.ApiConfig
import com.erthru.myroom.api.ApiConfig.Companion.IMG_URL
import com.erthru.myroom.api.model.SearchUserResponse
import com.erthru.myroom.ui.chat.ChatActivity
import com.erthru.myroom.utils.SharedPrefUser
import com.erthru.myroom.utils.UIHandler
import kotlinx.android.synthetic.main.activity_search_user.*
import kotlinx.android.synthetic.main.input.view.*
import retrofit2.Call
import retrofit2.Response

class SearchUserActivity : AppCompatActivity() {

    private var email:String? = null
    private var name:String? = null
    private var photo:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)

        dialogSearch()

        btnReSU.setOnClickListener { dialogSearch() }
        btnBackSU.setOnClickListener { this.finish() }
        btnChatSU.setOnClickListener {
            val i = Intent(this,ChatActivity::class.java)
            i.putExtra("friendName",name)
            i.putExtra("friendEmail",email)
            i.putExtra("friendPhoto",photo)
            startActivity(i)
        }

    }

    private fun dialogSearch(){

        val v = layoutInflater.inflate(R.layout.input,null)
        v.txInput.setHint("Email")

        AlertDialog.Builder(this)
            .setTitle("Search")
            .setView(v)
            .setPositiveButton("SEARCH") { dialogInterface, i ->
                if(v.txInput.text.toString().equals(SharedPrefUser(this).getEmail()) || v.txInput.text.toString().isNullOrBlank()){
                    UIHandler(this).showErrorToast("Search Failed.")
                    this.finish()
                }else{
                    searchUser(v.txInput.text.toString())
                }
            }
            .setNegativeButton("CANCEL") { dialogInterface, i ->
                dialogInterface.dismiss()
                finish()
            }
            .setOnCancelListener {
                finish()
            }
            .show()

    }


    private fun searchUser(email:String?){

        pbSU.visibility = View.VISIBLE
        ApiConfig().retrofit().searchUser(email)
            .enqueue(object : retrofit2.Callback<SearchUserResponse>{

                override fun onFailure(call: Call<SearchUserResponse>, t: Throwable) {
                    pbSU.visibility = View.GONE
                    Log.d("ONFAILURE",t.toString())
                    UIHandler(this@SearchUserActivity).showErrorToast("Search failed.")
                    finish()
                }

                override fun onResponse(call: Call<SearchUserResponse>, response: Response<SearchUserResponse>) {

                    pbSU.visibility = View.GONE

                    if(response?.isSuccessful){

                        if(response?.body()?.user != null){
                            onUserFound(
                                response?.body()?.user?.user_photo,
                                response?.body()?.user?.user_name,
                                response?.body()?.user?.user_email
                            )
                        }else{
                            onUserNotFound()
                        }

                    }

                }

            })

    }

    private fun onUserFound(img:String?, name:String?, email:String?){

        this.photo = img
        this.name = name
        this.email = email

        layoutUserSU.visibility = View.VISIBLE
        Glide.with(this@SearchUserActivity).load(IMG_URL+this.photo).into(imgSU)
        tvNameSU.text = name

    }

    private fun onUserNotFound(){

        AlertDialog.Builder(this)
            .setTitle("Information")
            .setMessage("User not found.")
            .setPositiveButton("CLOSE"){ dialogInterface, i ->
                dialogSearch()
            }
            .show()

    }

}
