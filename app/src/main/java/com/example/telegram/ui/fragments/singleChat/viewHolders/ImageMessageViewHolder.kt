package com.example.telegram.ui.fragments.singleChat.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.fragments.singleChat.views.MessageView
import com.example.telegram.utils.downloadAndSetImage
import com.example.telegram.utils.timeStampToTime
import kotlinx.android.synthetic.main.item_message_image.view.*

class ImageMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    // Sent image views
    private val sentImageBlock: ConstraintLayout = view.sent_image_block
    private val sentImage: ImageView = view.sent_image
    private val sentImageTime: TextView = view.sent_image_time

    // Received image views
    private val receivedImageBlock: ConstraintLayout = view.received_image_block
    private val receivedImage: ImageView = view.received_image
    private val receivedImageTime: TextView = view.received_image_time

    fun drawImageMessage(holder: ImageMessageViewHolder, view: MessageView) {

        if (view.sender == CURRENT_UID) {
            holder.receivedImageBlock.visibility = View.GONE
            holder.sentImageBlock.visibility = View.VISIBLE

            holder.sentImage.downloadAndSetImage(view.fileUrl)
            holder.sentImageTime.text =
                view.timeStamp.timeStampToTime()
        } else {
            holder.receivedImageBlock.visibility = View.VISIBLE
            holder.sentImageBlock.visibility = View.GONE

            holder.receivedImage.downloadAndSetImage(view.fileUrl)
            holder.receivedImageTime.text =
                view.timeStamp.timeStampToTime()
        }
    }
}