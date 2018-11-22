package com.erthru.myroom.api.model

data class SearchUserResponse(

    val error:Boolean?,
    val message:String?,
    val user:SearchUserUser?

)