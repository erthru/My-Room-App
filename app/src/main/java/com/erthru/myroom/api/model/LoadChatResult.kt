package com.erthru.myroom.api.model

data class LoadChatResult(

    val chat_id:Int?,
    val chat_body:String?,
    val chat_sender:String?,
    val chat_receiver:String?,
    val chat_unread:String?,
    val chat_created_at:String?

)