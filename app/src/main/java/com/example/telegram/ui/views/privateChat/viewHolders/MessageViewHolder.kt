package com.example.telegram.ui.views.privateChat.viewHolders

import com.example.telegram.ui.views.privateChat.views.MessageView

interface MessageViewHolder {

    fun drawMessage(view: MessageView)

    fun onAttach(view: MessageView)

    fun onDetach()
}