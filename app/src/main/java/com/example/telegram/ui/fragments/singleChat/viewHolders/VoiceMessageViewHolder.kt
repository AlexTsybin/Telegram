package com.example.telegram.ui.fragments.singleChat.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.fragments.singleChat.views.MessageView
import com.example.telegram.utils.timeStampToTime
import kotlinx.android.synthetic.main.item_message_voice.view.*

class VoiceMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    // Sent voice views
    private val sentVoiceBlock: ConstraintLayout = view.sent_voice_block
    val sentVoicePlayBtn: ImageView = view.sent_voice_play
    val sentVoiceStopBtn: ImageView = view.sent_voice_stop
    private val sentVoiceTime: TextView = view.sent_voice_time

    // Received voice views
    private val receivedVoiceBlock: ConstraintLayout = view.received_voice_block
    val receivedVoicePlayBtn: ImageView = view.received_voice_play
    val receivedVoiceStopBtn: ImageView = view.received_voice_stop
    private val receivedVoiceTime: TextView = view.received_voice_time

    fun drawVoiceMessage(holder: VoiceMessageViewHolder, view: MessageView) {

        if (view.sender == CURRENT_UID) {
            holder.receivedVoiceBlock.visibility = View.GONE
            holder.sentVoiceBlock.visibility = View.VISIBLE

            holder.sentVoiceTime.text =
                view.timeStamp.timeStampToTime()
        } else {
            holder.receivedVoiceBlock.visibility = View.VISIBLE
            holder.sentVoiceBlock.visibility = View.GONE

            holder.receivedVoiceTime.text =
                view.timeStamp.timeStampToTime()
        }
    }
}