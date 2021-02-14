package com.example.telegram.ui.views.privateChat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.ui.views.privateChat.viewHolders.*
import com.example.telegram.ui.views.privateChat.views.MessageView

class PrivateChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mMessagesCacheList = mutableListOf<MessageView>()
    private var mListViewHolders = mutableListOf<MessageViewHolder>()
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
        (holder as MessageViewHolder).drawMessage(mMessagesCacheList[position])
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        (holder as MessageViewHolder).onAttach(mMessagesCacheList[holder.adapterPosition])
        mListViewHolders.add(holder as MessageViewHolder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        (holder as MessageViewHolder).onDetach()
        mListViewHolders.remove(holder as MessageViewHolder)
    }

    override fun getItemViewType(position: Int): Int {
        return mMessagesCacheList[position].getViewType()
    }

    override fun getItemCount(): Int = mMessagesCacheList.size

    fun onDestroy() {
        mListViewHolders.forEach {
            it.onDetach()
        }
    }
}
