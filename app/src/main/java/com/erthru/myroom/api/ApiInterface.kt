package com.erthru.myroom.api

import com.erthru.myroom.api.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface{

    @GET("check_user.php")
    fun checkUser(
        @Query("email")email:String?
    ) : Call<CheckUserResponse>


    @GET("load_chat_room.php")
    fun loadChatRoom(
        @Query("email")email:String?
    ) : Call<LoadChatRoomResponse>


    @GET("load_chat.php")
    fun loadChat(
        @Query("user0")user0:String?,
        @Query("user1")user1:String?
    ) : Call<LoadChatResponse>


    @FormUrlEncoded
    @POST("new_chat.php")
    fun newChat(
        @Field("body")body:String?,
        @Field("sender")sender:String?,
        @Field("receiver")receiver:String?
    ) : Call<NewChatResponse>


    @Multipart
    @POST("new_user.php")
    fun newUser(
        @Part("email")email:RequestBody?,
        @Part("name")name:RequestBody?,
        @Part photo:MultipartBody.Part?
    ) : Call<NewUserResponse>

    @Multipart
    @POST("update_user.php")
    fun updateUser(
        @Part("email")email:RequestBody?,
        @Part("name")name:RequestBody?,
        @Part photo:MultipartBody.Part?
    ) : Call<UpdateUserResponse>

    @FormUrlEncoded
    @POST("update_user_without_img.php")
    fun updateUserWithoutIMG(
        @Field("email")email:String?,
        @Field("name")name:String?
    ) : Call<UpdateUserWithoutIMGResponse>


    @GET("user_detail.php")
    fun userDetail(
        @Query("email")email:String?
    ) : Call<UserDetailResponse>

    @GET("search_user.php")
    fun searchUser(
        @Query("email")email:String?
    ) : Call<SearchUserResponse>

    @GET("read_chat.php")
    fun readChat(
        @Query("sender")sender:String?,
        @Query("receiver")receiver:String?
    ) : Call<ReadChatResponse>

}