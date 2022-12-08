package com.cogniwide.cogniassist.requests

import com.cogniwide.cogniassist.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {

    val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(Constants.REST_URL)
        .addConverterFactory(GsonConverterFactory.create())

    val CHAT_BOT_API: ChatBotApi by lazy {
        retrofitBuilder.build()
            .create(ChatBotApi::class.java)
    }

}