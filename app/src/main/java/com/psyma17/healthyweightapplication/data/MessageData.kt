package com.psyma17.healthyweightapplication.data

data class MessageData(
    var uidSender: String = "",
    var dateSent: Long= 0,
    var edited: Boolean = false,
    var reported: Boolean = false,
    var message: String = ""
)
