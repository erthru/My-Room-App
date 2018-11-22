package com.erthru.myroom.ui.login

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.erthru.myroom.R
import com.erthru.myroom.api.ApiConfig
import com.erthru.myroom.api.model.CheckUserResponse
import com.erthru.myroom.ui.main.MainActivity
import com.erthru.myroom.ui.newuser.NewUserActivity
import com.erthru.myroom.utils.FCMInstanceID
import com.erthru.myroom.utils.GoogleLogin
import com.erthru.myroom.utils.UIHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener {
            btnLogin.visibility = View.GONE
            startActivityForResult(GoogleLogin(this).account().signInIntent,1)
        }

        btnInfoLogin.setOnClickListener {

            UIHandler(this).showDialog("Information","Dev : Erthru\nGithub : github.com/erthru")

        }

    }

    override fun onStart() {
        super.onStart()

        if(GoogleSignIn.getLastSignedInAccount(this) != null){
            checkUser(GoogleSignIn.getLastSignedInAccount(this)!!)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && data != null && resultCode == Activity.RESULT_OK){

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            if(task.getResult(ApiException::class.java) != null){
                checkUser(task.getResult(ApiException::class.java)!!)
            }else{
                UIHandler(this).showErrorToast("Login Failed")
                btnLogin.visibility = View.VISIBLE
            }

        }else{

            recreate()

        }

    }

    private fun checkUser(user:GoogleSignInAccount){

        ApiConfig().retrofit().checkUser(user.email)
            .enqueue(object : retrofit2.Callback<CheckUserResponse>{

                override fun onFailure(call: Call<CheckUserResponse>, t: Throwable) {
                    UIHandler(this@LoginActivity).showErrorToast("Login failed")
                    btnLogin.visibility = View.VISIBLE
                }

                override fun onResponse(call: Call<CheckUserResponse>, response: Response<CheckUserResponse>) {

                    if(response.isSuccessful){

                        if(response.body()?.message?.equals("exist")!!){
                            finish()
                            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                        }else{
                            finish()
                            startActivity(Intent(this@LoginActivity,NewUserActivity::class.java))
                        }

                    }

                }

            })

    }


}
