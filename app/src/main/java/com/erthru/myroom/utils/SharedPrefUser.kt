package com.erthru.myroom.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SharedPrefUser(val context: Context){

    val sp:SharedPreferences = context.getSharedPreferences("user",MODE_PRIVATE)
    val editor:SharedPreferences.Editor = sp.edit()

    fun saveData(email:String?, name:String?, photo:String?){
        editor.putString("email",email)
        editor.putString("name",name)
        editor.putString("photo",photo)
        editor.commit()
    }

    fun clearUser(){
        editor.clear()
        editor.commit()
    }

    fun getEmail() : String? = sp.getString("email",null)

    fun getName() : String? = sp.getString("name",null)

    fun getPhoto() : String? = sp.getString("photo",null)


}