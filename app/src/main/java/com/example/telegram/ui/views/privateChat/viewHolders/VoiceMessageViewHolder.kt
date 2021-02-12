package com.example.telegram.ui.views.privateChat.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.views.privateChat.views.MessageView
import com.example.telegram.utils.AppVoicePlayer
import com.example.telegram.utils.timeStampToTime
import kotlinx.android.synthetic.main.item_message_voice.view.*

class VoiceMessageViewHolder(view: View) : RecyclerView.ViewHolder(view), MessageViewHolder {

    // Sent voice views
    private val sentVoiceBlock: ConstraintLayout = view.sent_voice_block
    private val sentVoicePlayBtn: ImageView = view.sent_voice_play
    private val sentVoiceStopBtn: ImageView = view.sent_voice_stop
    private val sentVoiceTime: TextView = view.sent_voice_time

    // Received voice views
    private val receivedVoiceBlock: ConstraintLayout = view.received_voice_block
    private val receivedVoicePlayBtn: ImageView = view.received_voice_play
    private val receivedVoiceStopBtn: ImageView = view.received_voice_stop
    private val receivedVoiceTime: TextView = view.received_voice_time

    private val mAppVoicePlayer = AppVoicePlayer()

    override fun drawMessage(view: MessageView) {

        if (view.sender == CURRENT_UID) {
            receivedVoiceBlock.visibility = View.GONE
            sentVoiceBlock.visibility = View.VISIBLE

            sentVoiceTime.text =
                view.timeStamp.timeStampToTime()
        } else {
            receivedVoiceBlock.visibility = View.VISIBLE
            sentVoiceBlock.visibility = View.GONE

            receivedVoiceTime.text =
                view.timeStamp.timeStampToTime()
        }
    }

    override fun onAttach(view: MessageView) {

        mAppVoicePlayer.init()

        if (view.sender == CURRENT_UID) {
            sentVoicePlayBtn.setOnClickListener {
                sentVoicePlayBtn.visibility = View.GONE
                sentVoiceStopBtn.visibility = View.VISIBLE
                sentVoiceStopBtn.setOnClickListener {
                    stop {
                        sentVoiceStopBtn.setOnClickListener(null)
                        sentVoicePlayBtn.visibility = View.VISIBLE
                        sentVoiceStopBtn.visibility = View.GONE
                    }
                }
                play(view) {
                    sentVoicePlayBtn.visibility = View.VISIBLE
                    sentVoiceStopBtn.visibility = View.GONE
                }
            }
        } else {
            receivedVoicePlayBtn.setOnClickListener {
                receivedVoicePlayBtn.visibility = View.GONE
                receivedVoiceStopBtn.visibility = View.VISIBLE
                receivedVoiceStopBtn.setOnClickListener {
                    stop {
                        receivedVoiceStopBtn.setOnClickListener(null)
                        receivedVoicePlayBtn.visibility = View.VISIBLE
                        receivedVoiceStopBtn.visibility = View.GONE
                    }
                }
                play(view) {
                    receivedVoicePlayBtn.visibility = View.VISIBLE
                    receivedVoiceStopBtn.visibility = View.GONE
                }
            }
        }
    }

    private fun play(view: MessageView, function: () -> Unit) {

        mAppVoicePlayer.play(view.id, view.fileUrl) {
            function()
        }
    }

    private fun stop(function: () -> Unit) {
        mAppVoicePlayer.stop {
            function()
        }
    }

    override fun onDetach() {
        sentVoicePlayBtn.setOnClickListener(null)
        receivedVoicePlayBtn.setOnClickListener(null)
        mAppVoicePlayer.release()
    }
}