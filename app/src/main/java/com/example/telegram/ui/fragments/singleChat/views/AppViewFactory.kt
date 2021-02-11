package com.example.telegram.ui.fragments.singleChat.views

import com.example.telegram.models.CommonModel
import com.example.telegram.utils.MESSAGE_TYPE_IMAGE
import com.example.telegram.utils.MESSAGE_TYPE_TEXT

class AppViewFactory {
    companion object {
        fun getView(message: CommonModel): MessageView {
            return when (message.messageType) {
                MESSAGE_TYPE_IMAGE -> ImageMessageView(
                    message.id,
                    message.messageSender,
                    message.timeStamp.toString(),
                    message.fileUrl
                )
                else -> TextMessageView(
                    message.id,
                    message.messageSender,
                    message.timeStamp.toString(),
                    message.fileUrl,
                    message.messageText
                )
            }
        }
    }
}