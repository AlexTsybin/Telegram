package com.example.telegram.ui.fragments.singleChat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.utils.*
import kotlinx.android.synthetic.main.item_message.view.*

class SingleChatAdapter : RecyclerView.Adapter<SingleChatAdapter.SingleChatViewHolder>() {

    private var mMessagesCacheList = mutableListOf<CommonModel>()
    private lateinit var mDiffResult: DiffUtil.DiffResult

//    fun addItem(item: CommonModel, toBottom: Boolean) {
//        val newList = mutableListOf<CommonModel>()
//        newList.addAll(mMessagesCacheList)
//        if (!newList.contains(item)){
//            newList.add(item)
//        }
//        newList.sortBy { it.timeStamp.toString() }
//        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallback(mMessagesCacheList, newList))
//        mDiffResult.dispatchUpdatesTo(this)
//        mMessagesCacheList = newList
//    }

    fun addItemToTop(item: CommonModel, onSuccess: () -> Unit) {
        if (!mMessagesCacheList.contains(item)) {
            mMessagesCacheList.add(item)
            mMessagesCacheList.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun addItemToBottom(item: CommonModel, onSuccess: () -> Unit) {
        if (!mMessagesCacheList.contains(item)) {
            mMessagesCacheList.add(item)
            notifyItemInserted(mMessagesCacheList.size)
        }
        onSuccess()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return SingleChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: SingleChatViewHolder, position: Int) {
        when (mMessagesCacheList[position].messageType) {
            MESSAGE_TYPE_TEXT -> drawTextMessage(holder, position)
            MESSAGE_TYPE_IMAGE -> drawImageMessage(holder, position)
        }
    }

    private fun drawImageMessage(holder: SingleChatViewHolder, position: Int) {

        holder.sentMessageBlock.visibility = View.GONE
        holder.receivedMessageBlock.visibility = View.GONE

        if (mMessagesCacheList[position].messageSender == CURRENT_UID) {
            holder.receivedImageBlock.visibility = View.GONE
            holder.sentImageBlock.visibility = View.VISIBLE

            holder.sentImage.downloadAndSetImage(mMessagesCacheList[position].fileUrl)
            holder.sentImageTime.text =
                mMessagesCacheList[position].timeStamp.toString().timeStampToTime()
        } else {
            holder.receivedImageBlock.visibility = View.VISIBLE
            holder.sentImageBlock.visibility = View.GONE

            holder.receivedImage.downloadAndSetImage(mMessagesCacheList[position].fileUrl)
            holder.receivedImageTime.text =
                mMessagesCacheList[position].timeStamp.toString().timeStampToTime()
        }
    }

    private fun drawTextMessage(holder: SingleChatViewHolder, position: Int) {

        holder.receivedImageBlock.visibility = View.GONE
        holder.sentImageBlock.visibility = View.GONE

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

        // Text
        // Sent message views
        val sentMessageBlock: ConstraintLayout = view.sent_messages_block
        val sentMessage: TextView = view.chat_sent_message
        val sentMessageTime: TextView = view.sent_message_time

        // Received message views
        val receivedMessageBlock: ConstraintLayout = view.received_messages_block
        val receivedMessage: TextView = view.chat_received_message
        val receivedMessageTime: TextView = view.received_message_time

        // Image
        // Sent image views
        val sentImageBlock: ConstraintLayout = view.sent_image_block
        val sentImage: ImageView = view.sent_image
        val sentImageTime: TextView = view.sent_image_time

        // Received image views
        val receivedImageBlock: ConstraintLayout = view.received_image_block
        val receivedImage: ImageView = view.received_image
        val receivedImageTime: TextView = view.received_image_time
    }
}
