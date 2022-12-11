package com.cogniwide.cogniassist.models

import com.google.gson.annotations.SerializedName

data class BotMessage(
    @SerializedName("recipient_id")
    val recipient: String?= null,

    @SerializedName("text")
    val response: String?= null,

    @SerializedName("image")
    val imageUrl: String?= null,

    @SerializedName("buttons", alternate = ["quick_replies"])
    val buttons: List<Button>?=null

)