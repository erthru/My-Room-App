package com.erthru.myroom.utils

import android.content.Context
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.erthru.myroom.api.ApiConfig.Companion.BASE_URL
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import org.json.JSONObject
import kotlin.math.log

class FCMInstanceID() : FirebaseInstanceIdService() {

    var context:Context? = null

    constructor(context: Context?) : this(){
        this.context = context
    }



    override fun onTokenRefresh() {
        super.onTokenRefresh()

        Log.d("DEVICE_TOKEN",""+FirebaseInstanceId.getInstance().getToken())
        if(SharedPrefUser(this).getEmail() != null){
            saveToken(FirebaseInstanceId.getInstance().getToken())
        }

    }

    fun saveToken(token:String?){

        AndroidNetworking.post(BASE_URL+"save_user_token.php")
            .addBodyParameter("email",SharedPrefUser(context!!).getEmail())
            .addBodyParameter("token",token)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{

                override fun onResponse(response: JSONObject?) {
                    if(response?.getBoolean("error") == false){
                        Log.d("TOKEN_SAVED","SUCCESS")
                    }
                }

                override fun onError(anError: ANError?) {
                    Log.d("ONFAILURE",anError.toString())
                }

            })

    }

    fun removeToken(email:String?){
        AndroidNetworking.post(BASE_URL+"save_user_token.php")
            .addBodyParameter("email",SharedPrefUser(context!!).getEmail())
            .addBodyParameter("token","NULL")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{

                override fun onResponse(response: JSONObject?) {
                    if(response?.getBoolean("error") == false){
                        Log.d("TOKEN_SAVED","SUCCESS")
                    }
                }

                override fun onError(anError: ANError?) {
                    Log.d("ONFAILURE",anError.toString())
                }

            })
    }

    fun sendNotificationPrepare(email:String?,message:String?){

        AndroidNetworking.get(BASE_URL+"load_user_token.php?email=$email")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{

                override fun onResponse(response: JSONObject?) {

                    val token = response?.get("token").toString()
                    sendNotification(token,message)

                }

                override fun onError(anError: ANError?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })

    }

    private fun sendNotification(token:String?, message:String?){

        AndroidNetworking.get(BASE_URL+"send_notification.php?token=$token&body=$message")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{

                override fun onResponse(response: JSONObject?) {
                    Log.d("NOTIFICATION","SENT")
                }

                override fun onError(anError: ANError?) {
                    Log.d("ONERROR",anError.toString())
                }

            })

    }

}