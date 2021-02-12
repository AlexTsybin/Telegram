package com.example.telegram.ui.views.singleChat.viewHolders

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.database.getFileFromStorage
import com.example.telegram.ui.views.singleChat.views.MessageView
import com.example.telegram.utils.WRITE_FILE
import com.example.telegram.utils.checkPermission
import com.example.telegram.utils.showToast
import com.example.telegram.utils.timeStampToTime
import kotlinx.android.synthetic.main.item_message_file.view.*
import java.io.File
import java.lang.Exception

class FileMessageViewHolder(view: View) : RecyclerView.ViewHolder(view), MessageViewHolder {

    // Sent voice views
    private val sentFileBlock: ConstraintLayout = view.sent_file_block
    private val sentFileName: TextView = view.sent_file_name
    private val sentFilePic: ImageView = view.sent_file_pic
    private val sentFileProgressBar: ProgressBar = view.sent_pb
    private val sentFileTime: TextView = view.sent_file_time

    // Received voice views
    private val receivedFileBlock: ConstraintLayout = view.received_file_block
    private val receivedFileName: TextView = view.received_file_name
    private val receivedFilePic: ImageView = view.received_file_pic
    private val receivedFileProgressBar: ProgressBar = view.received_pb
    private val receivedFileTime: TextView = view.received_file_time

    override fun drawMessage(view: MessageView) {

        if (view.sender == CURRENT_UID) {
            receivedFileBlock.visibility = View.GONE
            sentFileBlock.visibility = View.VISIBLE

            sentFileTime.text = view.timeStamp.timeStampToTime()
            sentFileName.text = view.text
        } else {
            receivedFileBlock.visibility = View.VISIBLE
            sentFileBlock.visibility = View.GONE

            receivedFileTime.text = view.timeStamp.timeStampToTime()
            receivedFileName.text = view.text
        }
    }

    override fun onAttach(view: MessageView) {
        if (view.sender == CURRENT_UID){
            sentFilePic.setOnClickListener { clickOnFilePic(view) }
        } else {
            receivedFilePic.setOnClickListener { clickOnFilePic(view) }
        }
    }

    private fun clickOnFilePic(view: MessageView) {
        if (view.sender == CURRENT_UID){
            sentFilePic.visibility = View.INVISIBLE
            sentFileProgressBar.visibility = View.VISIBLE
        } else {
            receivedFilePic.visibility = View.INVISIBLE
            receivedFileProgressBar.visibility = View.VISIBLE
        }

        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), view.text)

        try {
            if (checkPermission(WRITE_FILE)){
                file.createNewFile()
                getFileFromStorage(file, view.fileUrl){
                    if (view.sender == CURRENT_UID){
                        sentFilePic.visibility = View.VISIBLE
                        sentFileProgressBar.visibility = View.INVISIBLE
                    } else {
                        receivedFilePic.visibility = View.VISIBLE
                        receivedFileProgressBar.visibility = View.INVISIBLE
                    }
                }
            }
        } catch (ex: Exception){
            showToast(ex.message.toString())
        }
    }

    override fun onDetach() {
        sentFilePic.setOnClickListener(null)
        receivedFilePic.setOnClickListener(null)
    }
}