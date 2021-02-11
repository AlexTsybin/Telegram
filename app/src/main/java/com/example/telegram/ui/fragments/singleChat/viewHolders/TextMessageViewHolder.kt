package com.example.telegram.ui.fragments.singleChat.viewHolders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.fragments.singleChat.views.MessageView
import com.example.telegram.utils.timeStampToTime
import kotlinx.android.synthetic.main.item_message_text.view.*

class TextMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    // Sent message views
    private val sentMessageBlock: ConstraintLayout = view.sent_messages_block
    private val sentMessage: TextView = view.chat_sent_message
    private val sentMessageTime: TextView = view.sent_message_time

    // Received message views
    private val receivedMessageBlock: ConstraintLayout = view.received_messages_block
    private val receivedMessage: TextView = view.chat_received_message
    private val receivedMessageTime: TextView = view.received_message_time

    fun drawTextMessage(holder: TextMessageViewHolder, view: MessageView) {

        if (view.sender == CURRENT_UID) {
            holder.sentMessageBlock.visibility = View.VISIBLE
            holder.receivedMessageBlock.visibility = View.GONE

            holder.sentMessage.text = view.text
            holder.sentMessageTime.text =
                view.timeStamp.timeStampToTime()
        } else {
            holder.sentMessageBlock.visibility = View.GONE
            holder.receivedMessageBlock.visibility = View.VISIBLE

            holder.receivedMessage.text = view.text
            holder.receivedMessageTime.text =
                view.timeStamp.timeStampToTime()
        }
    }
}