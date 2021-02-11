package com.example.telegram.ui.views.singleChat.views

interface MessageView {
    val id: String
    val sender: String
    val timeStamp: String
    val fileUrl: String
    val text: String

    companion object {
        val MESSAGE_IMAGE: Int
            get() = 0
        val MESSAGE_TEXT: Int
            get() = 1
        val MESSAGE_VOICE: Int
            get() = 2

    }

    fun getViewType(): Int
}