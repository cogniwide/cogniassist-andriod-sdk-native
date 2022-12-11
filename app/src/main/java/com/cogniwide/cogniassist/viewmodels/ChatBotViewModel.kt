package com.cogniwide.cogniassist.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.cogniwide.cogniassist.models.BotMessage
import com.cogniwide.cogniassist.models.Message
import com.cogniwide.cogniassist.models.UserMessage
import com.cogniwide.cogniassist.repositories.ChatBotRepository
import com.cogniwide.cogniassist.util.Constants

class ChatBotViewModel: ViewModel() {

    private var senderID: String = "default"

    private val TAG = "ChatBotViewModel"

    private val mChatBotRepository: ChatBotRepository = ChatBotRepository

    // constructor
    init {
        mChatBotRepository.setSenderID(senderID)
    }

    fun getBotMessages(): LiveData<List<BotMessage>> = mChatBotRepository.getBotMessages()

    fun getConversation(): LiveData<MutableList<Message>> = mChatBotRepository.getConversation()

    fun addUserMessageInConversation(message: String) {
        mChatBotRepository.addUserMessageInConversation(
            UserMessage(
                message = message,
                view = Constants.USER_VIEW
            )
        )
    }

    fun setSenderID(senderID: String) {
        this.senderID = senderID
    }

    fun queryBot(message: String) {
        mChatBotRepository.queryBot(message)
    }

}