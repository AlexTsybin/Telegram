package com.example.telegram.ui.views.privateChat.viewHolders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.views.privateChat.views.MessageView
import com.example.telegram.utils.timeStampToTime
import kotlinx.android.synthetic.main.item_message_text.view.*

class TextMessageViewHolder(view: View) : RecyclerView.ViewHolder(view), MessageViewHolder {

    // Sent message views
    private val sentMessageBlock: ConstraintLayout = view.sent_messages_block
    private val sentMessage: TextView = view.chat_sent_message
    private val sentMessageTime: TextView = view.sent_message_time

    // Received message views
    private val receivedMessageBlock: ConstraintLayout = view.received_messages_block
    private val receivedMessage: TextView = view.chat_received_message
    private val receivedMessageTime: TextView = view.received_message_time

    override fun drawMessage(view: MessageView) {

        if (view.sender == CURRENT_UID) {
            sentMessageBlock.visibility = View.VISIBLE
            receivedMessageBlock.visibility = View.GONE

            sentMessage.text = view.text
            sentMessageTime.text =
                view.timeStamp.timeStampToTime()
        } else {
            sentMessageBlock.visibility = View.GONE
            receivedMessageBlock.visibility = View.VISIBLE

            receivedMessage.text = view.text
            receivedMessageTime.text =
                view.timeStamp.timeStampToTime()
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }
}