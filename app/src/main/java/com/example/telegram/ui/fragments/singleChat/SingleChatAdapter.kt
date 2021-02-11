package com.example.telegram.ui.fragments.singleChat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.fragments.singleChat.viewHolders.AppViewHolderFactory
import com.example.telegram.ui.fragments.singleChat.viewHolders.ImageMessageViewHolder
import com.example.telegram.ui.fragments.singleChat.viewHolders.TextMessageViewHolder
import com.example.telegram.ui.fragments.singleChat.viewHolders.VoiceMessageViewHolder
import com.example.telegram.ui.fragments.singleChat.views.MessageView
import com.example.telegram.utils.downloadAndSetImage
import com.example.telegram.utils.timeStampToTime

class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mMessagesCacheList = mutableListOf<MessageView>()
//    private lateinit var mDiffResult: DiffUtil.DiffResult

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
            is ImageMessageViewHolder -> holder.drawImageMessage(holder, mMessagesCacheList[position])
            is VoiceMessageViewHolder -> holder.drawVoiceMessage(holder, mMessagesCacheList[position])
            is TextMessageViewHolder -> holder.drawTextMessage(holder, mMessagesCacheList[position])
            else -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mMessagesCacheList[position].getViewType()
    }

    override fun getItemCount(): Int = mMessagesCacheList.size
}
