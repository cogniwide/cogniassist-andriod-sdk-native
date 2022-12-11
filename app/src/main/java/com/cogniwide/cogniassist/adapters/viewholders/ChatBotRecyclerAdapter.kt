package com.cogniwide.cogniassist.adapters.viewholders

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cogniwide.cogniassist.ChatBotActivity
import com.cogniwide.cogniassist.R
import com.cogniwide.cogniassist.models.Message
import com.cogniwide.cogniassist.util.Constants

class ChatBotRecyclerAdapter// constructor
    (context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "ChatBotRecyclerAdapter"

    private var mConversation: MutableList<Message> = mutableListOf()

    private var mContext: Context? = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var view: View?= null

        when(viewType) {

            Constants.USER_VIEW -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.user_message_layout, parent, false)
                return UserMessageViewHolder(view)
            }

            Constants.BOT_VIEW -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.bot_message_layout, parent, false)
                return BotMessageViewHolder(view)
            }

            Constants.LOADING_VIEW -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.loading_bot_layout, parent, false)
                return LoadingBotViewHolder(view)
            }

            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.user_message_layout, parent, false)
                return UserMessageViewHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val itemViewType: Int = getItemViewType(position)

        if(itemViewType == Constants.USER_VIEW) {

            val userMessageViewHolder = holder as UserMessageViewHolder
            userMessageViewHolder.userMessageTV.text = mConversation[position].message?: Constants.MESSAGE_TEXT_NULL

        } else if(itemViewType == Constants.BOT_VIEW) {
                val botMessageViewHolder = holder as BotMessageViewHolder
                botMessageViewHolder.botBtnMessage.removeAllViews()

                if (mConversation[position].message!=null)  {
                    botMessageViewHolder.botTextMessage.visibility = View.VISIBLE
                    botMessageViewHolder.botTextMessage.text = mConversation[position].message
                }

                if (mConversation[position].buttons!=null){
                    botMessageViewHolder.botBtnMessage.visibility = View.VISIBLE


                    for(button in mConversation[position].buttons!!) {
                        // create button
                        val btn =  Button(holder.itemView.context)
                        btn.text = button.title
                        btn.setOnClickListener {
                            Log.d(TAG, "onBindViewHolder: button clicked: ${button.title}")
                            if (mContext is ChatBotActivity) {
                                (mContext as ChatBotActivity).addUserMessageInConversation(button.title)
                                (mContext as ChatBotActivity).queryBot(button.payload)
                            }
                        }
                        botMessageViewHolder.botBtnMessage.addView(btn)
                    }
                }

               if( mConversation[position].imageUrl!=null){
                    botMessageViewHolder.botImageMessage.visibility = View.VISIBLE

                    val requestOptions: RequestOptions = RequestOptions()
                        .placeholder(R.drawable.loading_img)

                    Glide.with(holder.itemView.context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(mConversation[position].imageUrl)
                        .into(botMessageViewHolder.botImageMessage)
                }

                if (mConversation[position].message==null && mConversation[position].buttons==null && mConversation[position].imageUrl==null){
                    botMessageViewHolder.botTextMessage.visibility = View.VISIBLE
                    botMessageViewHolder.botImageMessage.visibility = View.GONE
                    botMessageViewHolder.botTextMessage.text = Constants.MESSAGE_TEXT_NULL
                }
        }

    }

    override fun getItemCount(): Int = mConversation.size


    override fun getItemViewType(position: Int): Int {
        return mConversation[position].view
    }

    fun setConversation(conversation: MutableList<Message>) {
        Log.d(TAG,"new conversation set: $conversation")
        mConversation = conversation
        notifyDataSetChanged()
    }

    fun showBotLoading() {

        if(isBotLoading()) return

        mConversation.add(
            Message(
                message = null,
                view = Constants.LOADING_VIEW
        ))
        Log.d(TAG,"Showing loading")
        notifyDataSetChanged()
    }

    private fun isBotLoading(): Boolean  {
        Log.d(TAG,"isBotLoading: $mConversation")
        if(mConversation.isEmpty()) return false

        return mConversation[mConversation.size-1].view==Constants.LOADING_VIEW
    }

    fun hideBotLoading() {
        if(!isBotLoading()) {
            Log.d(TAG,"Bot is not loading")
            return
        }

        mConversation.removeAt(mConversation.size-1)
        notifyDataSetChanged()
    }

}