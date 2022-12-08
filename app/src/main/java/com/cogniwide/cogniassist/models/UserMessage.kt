package com.cogniwide.cogniassist.models

import com.cogniwide.cogniassist.util.Constants

data class UserMessage(
    var message: String?= null,
    var view: Int = Constants.USER_VIEW,
    var id: String?= null
)
