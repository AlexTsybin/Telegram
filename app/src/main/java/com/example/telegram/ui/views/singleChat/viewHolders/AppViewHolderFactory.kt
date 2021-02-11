package com.example.telegram.ui.views.singleChat.viewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.ui.views.singleChat.views.MessageView

class AppViewHolderFactory {
    companion object {
        fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                MessageView.MESSAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_image, parent, false)
                    ImageMessageViewHolder(view)
                }
                MessageView.MESSAGE_VOICE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_voice, parent, false)
                    VoiceMessageViewHolder(view)
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