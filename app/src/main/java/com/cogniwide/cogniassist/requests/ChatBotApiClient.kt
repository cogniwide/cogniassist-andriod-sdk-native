package com.cogniwide.cogniassist.requests

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cogniwide.cogniassist.models.BotMessage
import com.cogniwide.cogniassist.models.Message
import com.cogniwide.cogniassist.models.UserMessage
import com.cogniwide.cogniassist.util.AppExecutors
import com.cogniwide.cogniassist.util.Constants
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

object ChatBotApiClient {

    private const val TAG = "BotApiClient"
    private val mBotResponse: MutableLiveData<List<BotMessage>> = MutableLiveData()
    private val mConversation: MutableLiveData<MutableList<Message>> = MutableLiveData()

    // initalize ChatBotSocketAPI
    private val mChatBotSocketAPI: ChatBotSocketAPI = ChatBotSocketAPI()

    fun setSenderID(senderID: String) {
        mChatBotSocketAPI.setSenderID(senderID)
    }

    init {
       // set it to false for production
        mChatBotSocketAPI.isDebug(true)

        mChatBotSocketAPI.start(
            callback = object : ChatBotSocketAPI.Callback {
                override fun onStarted() {
                    // log
                    Log.d(TAG, "onStarted: ")
                    mChatBotSocketAPI.send("/default/welcome")
                }

                override fun onMessageReceived(message: BotMessage) {
                    Log.d(TAG, "onMessageReceived: $message")
                    val botResponseList = ArrayList<BotMessage>()
                    botResponseList.add(message)

                    mBotResponse.postValue(botResponseList)

                    var prevConversations: List<Message>? = mConversation.value

                    var botMessages: MutableList<Message> = mutableListOf()

                    for(response in botResponseList) {
                        botMessages.add(
                            Message(
                                message = response.response,
                                view = Constants.BOT_VIEW,
                                imageUrl = response.imageUrl,
                                buttons = response.buttons
                            ))
                    }

                    if(prevConversations==null) {
                        Log.d(TAG, "No Conversations found")
                        mConversation.postValue(botMessages)

                    } else {
                        Log.d(TAG, "old Conversations found")

                        var oldConversation: MutableList<Message>? = mConversation.value
                        oldConversation?.let { list ->

                            // hide loading first
                            if(list[list.size-1].view==Constants.LOADING_VIEW) list.removeAt(list.size-1)

                            list.addAll(botMessages)
                            mConversation.postValue(list)
                        }
                    }
                }
            }
        )
    }


    fun getBotMessages(): LiveData<List<BotMessage>>  = mBotResponse

    fun getConversation(): LiveData<MutableList<Message>> = mConversation

    fun addUserMessageInConversation(userMessage: UserMessage) {
        var oldConversation: MutableList<Message>? = mConversation.value

        if(oldConversation==null) {
            var newConversation: MutableList<Message> = mutableListOf()
            newConversation.add(Message(message = userMessage.message, view = Constants.USER_VIEW))
            newConversation.add(Message(message = null, view=Constants.LOADING_VIEW))
            mConversation.postValue(newConversation)
        } else {
            oldConversation.add(Message(message = userMessage.message, view = Constants.USER_VIEW))
            oldConversation.add(Message(message = null, view=Constants.LOADING_VIEW))
            mConversation.postValue(oldConversation)
        }
    }

    fun queryBot( message: String) {
        mChatBotSocketAPI.send(message)
    }

    fun close() {
        mChatBotSocketAPI.stop()
    }
}