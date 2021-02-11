package com.example.telegram.ui.views.singleChat.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.views.singleChat.views.MessageView
import com.example.telegram.utils.downloadAndSetImage
import com.example.telegram.utils.timeStampToTime
import kotlinx.android.synthetic.main.item_message_image.view.*

class ImageMessageViewHolder(view: View) : RecyclerView.ViewHolder(view), MessageViewHolder {

    // Sent image views
    private val sentImageBlock: ConstraintLayout = view.sent_image_block
    private val sentImage: ImageView = view.sent_image
    private val sentImageTime: TextView = view.sent_image_time

    // Received image views
    private val receivedImageBlock: ConstraintLayout = view.received_image_block
    private val receivedImage: ImageView = view.received_image
    private val receivedImageTime: TextView = view.received_image_time

    override fun drawMessage(view: MessageView) {

        if (view.sender == CURRENT_UID) {
            receivedImageBlock.visibility = View.GONE
            sentImageBlock.visibility = View.VISIBLE

            sentImage.downloadAndSetImage(view.fileUrl)
            sentImageTime.text =
                view.timeStamp.timeStampToTime()
        } else {
            receivedImageBlock.visibility = View.VISIBLE
            sentImageBlock.visibility = View.GONE

            receivedImage.downloadAndSetImage(view.fileUrl)
            receivedImageTime.text =
                view.timeStamp.timeStampToTime()
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }
}