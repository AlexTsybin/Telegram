package com.example.telegram.ui.views.privateChat.views

data class ImageMessageView(
    override val id: String,
    override val sender: String,
    override val timeStamp: String,
    override val fileUrl: String,
    override val text: String = ""
) : MessageView {
    override fun getViewType(): Int {
        return MessageView.MESSAGE_IMAGE
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }
}