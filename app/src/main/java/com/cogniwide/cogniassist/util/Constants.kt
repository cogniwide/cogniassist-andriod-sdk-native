package com.cogniwide.cogniassist.util

class Constants {

    companion object {
//        val COGNIASSIST_URL = "https://bots.cogniassist.com/5f36574c77643d1400290950/default"
        val COGNIASSIST_URL = "https://external-proxy.lycabot.com/bots/638f36c036af37a93533cd91/default"
        val REST_URL = "$COGNIASSIST_URL/webhooks/rest/"
        val NETWORK_TIMEOUT = 5000L
        val MESSAGE_TEXT_NULL = "Un supported chat element"

        val USER_VIEW = 0
        val BOT_VIEW = 1
        val LOADING_VIEW = 2
    }
}