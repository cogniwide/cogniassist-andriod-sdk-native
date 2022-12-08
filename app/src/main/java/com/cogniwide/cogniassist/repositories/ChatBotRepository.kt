package com.cogniwide.cogniassist.repositories

import androidx.lifecycle.LiveData
import com.cogniwide.cogniassist.models.BotMessage
import com.cogniwide.cogniassist.models.Message
import com.cogniwide.cogniassist.models.UserMessage
import com.cogniwide.cogniassist.requests.ChatBotApiClient

object ChatBotRepository {

    private const val TAG = "ChatBotRepository"

    private val mChatBotApiClient: ChatBotApiClient = ChatBotApiClient

    fun getBotMessages(): LiveData<List<BotMessage>> = mChatBotApiClient.getBotMessages()

    fun getConversation(): LiveData<MutableList<Message>> = mChatBotApiClient.getConversation()

    fun addUserMessageInConversation(userMessage: UserMessage) {
        mChatBotApiClient.addUserMessageInConversation(userMessage)
    }


    fun queryBot(senderID: String, message: String) {
        mChatBotApiClient.queryBot(senderID, message)
    }
}