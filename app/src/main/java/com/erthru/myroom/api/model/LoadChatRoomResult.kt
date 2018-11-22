package com.erthru.myroom.api.model

data class LoadChatRoomResult(

    val chat_id:Int?,
    val a_chat_sender:String?,
    val a_chat_receiver:String?,
    val chat_created_at:String?,
    val chat_sender_name:String?,
    val chat_receiver_name:String?,
    val chat_sender_photo:String?,
    val chat_receiver_photo:String?,
    val chat_body:String?,
    val chat_unread:String?

)