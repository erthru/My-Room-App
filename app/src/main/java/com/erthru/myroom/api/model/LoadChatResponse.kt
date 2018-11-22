package com.erthru.myroom.api.model

data class LoadChatResponse(

    val error:Boolean?,
    val message:String?,
    val result:ArrayList<LoadChatResult>?

)