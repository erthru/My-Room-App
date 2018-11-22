package com.erthru.myroom.api.model

data class LoadChatRoomResponse(

    val error:Boolean?,
    val message:String?,
    val result:ArrayList<LoadChatRoomResult>?

)