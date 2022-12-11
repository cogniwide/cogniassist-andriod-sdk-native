package com.cogniwide.cogniassist.models

import com.cogniwide.cogniassist.util.Constants

data class Button(
    val title: String,
    val payload: String
)

data class Message(
    var message: String?= null,
    var view: Int = Constants.USER_VIEW,
    var sender: String?= null,
    var imageUrl: String?=null,
    var buttons: List<Button>?=null
)
