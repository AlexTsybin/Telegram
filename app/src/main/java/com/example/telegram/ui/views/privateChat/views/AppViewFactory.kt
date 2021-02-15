package com.example.telegram.ui.views.privateChat.views

import com.example.telegram.models.CommonModel
import com.example.telegram.utils.MESSAGE_TYPE_FILE
import com.example.telegram.utils.MESSAGE_TYPE_IMAGE
import com.example.telegram.utils.MESSAGE_TYPE_VOICE

class AppViewFactory {
    companion object {
        fun getView(message: CommonModel): MessageView {
            return when (message.type) {
                MESSAGE_TYPE_IMAGE -> ImageMessageView(
                    message.id,
                    message.messageSender,
                    message.timeStamp.toString(),
                    message.fileUrl
                )
                MESSAGE_TYPE_VOICE -> VoiceMessageView(
                    message.id,
                    message.messageSender,
                    message.timeStamp.toString(),
                    message.fileUrl
                )
                MESSAGE_TYPE_FILE -> FileMessageView(
                    message.id,
                    message.messageSender,
                    message.timeStamp.toString(),
                    message.fileUrl,
                    message.messageText
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