package com.cogniwide.cogniassist.requests
import android.util.Log
import com.cogniwide.cogniassist.models.BotMessage
import io.socket.emitter.Emitter;
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject


class ChatBotSocketAPI
{
    private val EVENT_USER_MESSAGE = "user_uttered"
    private val EVENT_BOT_MESSAGE = "bot_uttered"
    private val EVENT_SESSION_REQUEST = "session_request"
    private val EVENT_SESSION_CONFIRM = "session_confirm"

    interface Callback
    {

        /**
         * Gets called when a successful connection has been made with the chatbot
         */
        fun onStarted()

        /**
         * Gets called every time a message *from the bot* has been received
         * @message: the text message from the chatbot
         */
        fun onMessageReceived(messages: BotMessage)
    }

    companion object
    {
        private const val TAG = "WEB SOCKET"

        private val GSON = Gson()
    }


    /**
     * If turned to true, will display verbose logs
     */
    var debug: Boolean = false

    // const webSocket
    private var  webSocket: Socket? = null

    private var callback: Callback? = null

    private var started = false

    private var senderID: String = "default"

    fun setSenderID(senderID: String) {
        this.senderID = senderID
    }

    fun isDebug(debug: Boolean)
    {
        this.debug = debug
    }


    /**
     * Sends asynchronously a text message to the chatbot.
     */
    fun send(message: String)
    {
        if (webSocket == null)
        {
            Log.e(TAG, "Websocket is not connected")
            return
        }

        // create new json object
        val obj = JSONObject()
        obj.put("session_id", senderID)
        obj.put("message", message)
        webSocket?.emit(EVENT_USER_MESSAGE, obj)
    }

    /**
     * Starts asynchronously a WebSocket connection with the Cogniassist Bot.
     * When started, the onStarted() method from the @callback will be called.
     */
    fun start(callback: Callback)
    {
        this.callback = callback
        this.started = true
//        startWebSocket("https://external-proxy.lycabot.com",
//            "/bots/638f36c036af37a93533cd91/default/socket.io")
        startWebSocket("wss://bots.cogniassist.com/",
            "/6322fb6542f2593af760ca4c/default/socket.io")
    }

    /**
     * Closes the WebSocket of the Bot.
     * Any further call to send() method will throw an exception
     */
    fun stop()
    {
        this.started = false
        webSocket?.close();
    }

    private fun startWebSocket(socketURL: String, socketPath: String )
    {
        // connecting
        Log.d(TAG, "Connecting to streamUrl")

        webSocket = IO.socket(socketURL, IO.Options().apply {
            path = socketPath
            transports = arrayOf("websocket")
            reconnection = true
        })

        webSocket?.on(Socket.EVENT_CONNECT){
            if (this.debug) Log.d(TAG, "Connected")
            val obj = JSONObject()
            obj.put("session_id", senderID)
            this.webSocket?.emit(EVENT_SESSION_REQUEST, obj)
        }

        webSocket?.on(EVENT_SESSION_CONFIRM) {
            if (this.debug) Log.d(TAG, "Session confirmed")
            callback?.onStarted()
        }

        webSocket?.on(EVENT_BOT_MESSAGE) {
            if (this.debug) Log.d(TAG, "Message received")
            val message = GSON.fromJson(it[0].toString(), BotMessage::class.java)
            callback?.onMessageReceived(message)
        }

        webSocket?.on(Socket.EVENT_DISCONNECT) {
            if (this.debug) Log.d(TAG, "Disconnected")
        }
        webSocket?.connect()

    }

}