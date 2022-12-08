package com.cogniwide.cogniassist.requests

import com.cogniwide.cogniassist.models.BotMessage
import com.cogniwide.cogniassist.models.Message
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatBotApi {

    @POST("webhook")
    fun messageBot(@Body userMessage:Message): Call<ArrayList<BotMessage>>
}