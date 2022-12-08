package com.cogniwide.cogniassist.models

import com.cogniwide.cogniassist.util.Constants

data class Message(
    var message: String?= null,
    var view: Int = Constants.USER_VIEW,
    var sender: String?= null,
    var imageUrl: String?=null
)
