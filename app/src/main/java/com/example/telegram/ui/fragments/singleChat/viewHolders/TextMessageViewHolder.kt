package com.example.telegram.ui.fragments.singleChat.viewHolders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message_text.view.*

class TextMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    // Sent message views
    val sentMessageBlock: ConstraintLayout = view.sent_messages_block
    val sentMessage: TextView = view.chat_sent_message
    val sentMessageTime: TextView = view.sent_message_time

    // Received message views
    val receivedMessageBlock: ConstraintLayout = view.received_messages_block
    val receivedMessage: TextView = view.chat_received_message
    val receivedMessageTime: TextView = view.received_message_time
}