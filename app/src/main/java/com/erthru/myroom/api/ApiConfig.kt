package com.erthru.myroom.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig{

    companion object {

        const val BASE_URL = "http://192.168.1.65/anows/myroom/"
        const val IMG_URL = "http://192.168.1.65/anows/myroom/img/"

    }

    fun retrofit() : ApiInterface{

        val connect = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return connect.create(ApiInterface::class.java)

    }

}