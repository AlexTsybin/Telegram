package com.example.telegram.ui.fragments.singleChat.viewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.ui.fragments.singleChat.views.MessageView

class AppViewHolderFactory {
    companion object {
        fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                MessageView.MESSAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_image, parent, false)
                    ImageMessageViewHolder(view)
                }
                else -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_text, parent, false)
                    TextMessageViewHolder(view)
                }
            }
        }
    }
}