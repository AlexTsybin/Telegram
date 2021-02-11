package com.example.telegram.ui.fragments.singleChat.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message_image.view.*

class ImageMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    // Sent image views
    val sentImageBlock: ConstraintLayout = view.sent_image_block
    val sentImage: ImageView = view.sent_image
    val sentImageTime: TextView = view.sent_image_time

    // Received image views
    val receivedImageBlock: ConstraintLayout = view.received_image_block
    val receivedImage: ImageView = view.received_image
    val receivedImageTime: TextView = view.received_image_time
}