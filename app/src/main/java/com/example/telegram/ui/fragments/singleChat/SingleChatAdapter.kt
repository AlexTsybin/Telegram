package com.example.telegram.ui.fragments.singleChat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.fragments.singleChat.viewHolders.AppViewHolderFactory
import com.example.telegram.ui.fragments.singleChat.viewHolders.ImageMessageViewHolder
import com.example.telegram.ui.fragments.singleChat.viewHolders.TextMessageViewHolder
import com.example.telegram.ui.fragments.singleChat.views.MessageView
import com.example.telegram.utils.downloadAndSetImage
import com.example.telegram.utils.timeStampToTime

class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mMessagesCacheList = mutableListOf<MessageView>()
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

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit) {
        if (!mMessagesCacheList.contains(item)) {
            mMessagesCacheList.add(item)
            mMessagesCacheList.sortBy { it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit) {
        if (!mMessagesCacheList.contains(item)) {
            mMessagesCacheList.add(item)
            notifyItemInserted(mMessagesCacheList.size)
        }
        onSuccess()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppViewHolderFactory.getViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageMessageViewHolder -> drawImageMessage(holder, position)
            is TextMessageViewHolder -> drawTextMessage(holder, position)
            else -> {}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mMessagesCacheList[position].getViewType()
    }

    private fun drawImageMessage(holder: ImageMessageViewHolder, position: Int) {

        if (mMessagesCacheList[position].sender == CURRENT_UID) {
            holder.receivedImageBlock.visibility = View.GONE
            holder.sentImageBlock.visibility = View.VISIBLE

            holder.sentImage.downloadAndSetImage(mMessagesCacheList[position].fileUrl)
            holder.sentImageTime.text =
                mMessagesCacheList[position].timeStamp.timeStampToTime()
        } else {
            holder.receivedImageBlock.visibility = View.VISIBLE
            holder.sentImageBlock.visibility = View.GONE

            holder.receivedImage.downloadAndSetImage(mMessagesCacheList[position].fileUrl)
            holder.receivedImageTime.text =
                mMessagesCacheList[position].timeStamp.timeStampToTime()
        }
    }

    private fun drawTextMessage(holder: TextMessageViewHolder, position: Int) {

        if (mMessagesCacheList[position].sender == CURRENT_UID) {
            holder.sentMessageBlock.visibility = View.VISIBLE
            holder.receivedMessageBlock.visibility = View.GONE

            holder.sentMessage.text = mMessagesCacheList[position].text
            holder.sentMessageTime.text =
                mMessagesCacheList[position].timeStamp.timeStampToTime()
        } else {
            holder.sentMessageBlock.visibility = View.GONE
            holder.receivedMessageBlock.visibility = View.VISIBLE

            holder.receivedMessage.text = mMessagesCacheList[position].text
            holder.receivedMessageTime.text =
                mMessagesCacheList[position].timeStamp.timeStampToTime()
        }
    }

    override fun getItemCount(): Int = mMessagesCacheList.size
}
