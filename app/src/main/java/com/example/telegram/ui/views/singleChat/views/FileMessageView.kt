package com.example.telegram.ui.views.singleChat.views

data class FileMessageView(
    override val id: String,
    override val sender: String,
    override val timeStamp: String,
    override val fileUrl: String,
    override val text: String = ""
) : MessageView {
    override fun getViewType(): Int {
        return MessageView.MESSAGE_FILE
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }
}