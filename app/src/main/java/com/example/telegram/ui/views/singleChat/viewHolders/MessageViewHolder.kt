package com.example.telegram.ui.views.singleChat.viewHolders

import com.example.telegram.ui.views.singleChat.views.MessageView

interface MessageViewHolder {

    fun drawMessage(view: MessageView)

    fun onAttach(view: MessageView)

    fun onDetach()
}