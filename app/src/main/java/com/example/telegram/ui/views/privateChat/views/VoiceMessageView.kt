package com.example.telegram.ui.views.privateChat.views

data class VoiceMessageView(
    override val id: String,
    override val sender: String,
    override val timeStamp: String,
    override val fileUrl: String,
    override val text: String = ""
) : MessageView {
    override fun getViewType(): Int {
        return MessageView.MESSAGE_VOICE
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }
}