package com.example.telegram.ui.fragments.singleChat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.utils.CURRENT_UID
import com.example.telegram.utils.DiffUtilCallback
import com.example.telegram.utils.timeStampToTime
import kotlinx.android.synthetic.main.item_message.view.*
import java.text.SimpleDateFormat
import java.util.*

class SingleChatAdapter : RecyclerView.Adapter<SingleChatAdapter.SingleChatViewHolder>() {

    private var mMessagesCacheList = emptyList<CommonModel>()
    private lateinit var mDiffResult: DiffUtil.DiffResult

    fun addItem(item: CommonModel) {
        val newList = mutableListOf<CommonModel>()
        newList.addAll(mMessagesCacheList)
        if (!newList.contains(item)){
            newList.add(item)
        }
        newList.sortBy { it.timeStamp.toString() }
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallback(mMessagesCacheList, newList))
        mDiffResult.dispatchUpdatesTo(this)
        mMessagesCacheList = newList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return SingleChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: SingleChatViewHolder, position: Int) {
        if (mMessagesCacheList[position].messageSender == CURRENT_UID) {
            holder.sentMessageBlock.visibility = View.VISIBLE
            holder.receivedMessageBlock.visibility = View.GONE

            holder.sentMessage.text = mMessagesCacheList[position].messageText
            holder.sentMessageTime.text =
                mMessagesCacheList[position].timeStamp.toString().timeStampToTime()
        } else {
            holder.sentMessageBlock.visibility = View.GONE
            holder.receivedMessageBlock.visibility = View.VISIBLE

            holder.receivedMessage.text = mMessagesCacheList[position].messageText
            holder.receivedMessageTime.text =
                mMessagesCacheList[position].timeStamp.toString().timeStampToTime()
        }
    }

    override fun getItemCount(): Int = mMessagesCacheList.size

    class SingleChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // Sent message views
        val sentMessageBlock: ConstraintLayout = view.sent_messages_block
        val sentMessage: TextView = view.chat_sent_message
        val sentMessageTime: TextView = view.sent_message_time

        // Received message views
        val receivedMessageBlock: ConstraintLayout = view.received_messages_block
        val receivedMessage: TextView = view.chat_received_message
        val receivedMessageTime: TextView = view.received_message_time
    }
}
