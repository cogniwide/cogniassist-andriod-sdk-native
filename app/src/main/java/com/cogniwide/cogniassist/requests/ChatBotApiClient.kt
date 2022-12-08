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
    private var mBotQueryRunnable: BotQueryRunnable?= null



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

    private fun addBotMessageInConversation(botMessage: Message) {
        var oldConversation: MutableList<Message>? = mConversation.value

        if(oldConversation==null) {
            var newConversation: MutableList<Message> = mutableListOf()
            newConversation.add(botMessage)
            mConversation.postValue(newConversation)
        } else {
            if(oldConversation[oldConversation.size-1].view==Constants.LOADING_VIEW)
                oldConversation.removeAt(oldConversation.size-1)

            oldConversation.add(botMessage)
            mConversation.postValue(oldConversation)
        }
    }

    private fun isBotLoading(): Boolean{

        val currentConversation = mConversation.value ?: return false

        return (currentConversation.isNotEmpty() && currentConversation[currentConversation.size-1].view==Constants.LOADING_VIEW)
    }

    fun queryBot(senderID: String, message: String) {

        if(mBotQueryRunnable!=null) mBotQueryRunnable = null

        mBotQueryRunnable = BotQueryRunnable(senderID, message)

        val handler: Future<*> = AppExecutors.networkIO().submit(mBotQueryRunnable)

        AppExecutors.networkIO().schedule(Runnable {

            // stop request - timeout occurred
            handler.cancel(true)

            if(isBotLoading()) {
                addBotMessageInConversation(
                    Message(
                        message = "Bot is taking too long to process your request. Please try again after some time",
                        view = Constants.BOT_VIEW
                    )
                )
            }

        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)

    }



    private class BotQueryRunnable(
        private var senderID: String,
        private var message: String,
        var cancelRequest: Boolean = false
    ): Runnable {


        override fun run() {

            try {

                val response:Response<*> = getMessageBot(senderID, message).execute()
                if(cancelRequest) return

                if(response.code()==200) {
                    Log.d(TAG,"Response code 200 for bot query API, response: ${response.body()}")

                    val botResponseList: ArrayList<BotMessage> = (response.body() as ArrayList<BotMessage>)

                    botResponseList?.let {
                        mBotResponse.postValue(botResponseList)

                        var prevConversations: List<Message>? = mConversation.value

                        var botMessages: MutableList<Message> = mutableListOf()

                        for(response in botResponseList) {
                            botMessages.add(
                                Message(
                                    message = response.response,
                                    view = Constants.BOT_VIEW,
                                    imageUrl = response.imageUrl
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

                } else {
                    Log.d(TAG,"Response code: ${response.code()}")
                    addBotMessageInConversation(
                        Message(
                            message = "Wrong response. \n\nCode: ${response.code()}\nMessage: ${response.message()}",
                            view = Constants.BOT_VIEW
                        )
                    )
                }


            } catch (e: Exception) {
                Log.d(TAG,"Exception: ${e.message}")
                e.printStackTrace()
                addBotMessageInConversation(
                    Message(
                        message = "Exception Occurred. \n\nMessage: ${e.message}",
                        view = Constants.BOT_VIEW
                    )
                )
            }

        }


        private fun getMessageBot(senderID: String, message: String): Call<ArrayList<BotMessage>>
                = ServiceGenerator.CHAT_BOT_API.messageBot(
            userMessage = Message(
                message= message,
                sender = senderID
            )
        )

        // cancel bot request - may be on back press
        fun cancelRequest() {
            Log.d(TAG,"Cancelling the Bot Query Request")
            cancelRequest = true
        }
    }




}