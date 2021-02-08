package com.example.telegram.ui.fragments

import android.view.View
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.models.UserModel
import com.example.telegram.utils.*
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.toolbar_chat.view.*

class SingleChatFragment(private val contact: CommonModel) : BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var mListenerChatToolbar: AppValueEventListener
    private lateinit var mReceivedContact:UserModel
    private lateinit var mToolbarChat:View
    private lateinit var mRefContact: DatabaseReference

    override fun onResume() {
        super.onResume()
        mToolbarChat = APP_ACTIVITY.mToolbar.toolbar_chat
        mToolbarChat.visibility = View.VISIBLE
        mListenerChatToolbar = AppValueEventListener {
            mReceivedContact = it.getUserModel()
            initToolbarChat()
        }

        mRefContact = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefContact.addValueEventListener(mListenerChatToolbar)
    }

    private fun initToolbarChat() {
        if (mReceivedContact.fullname.isEmpty()){
            mToolbarChat.toolbar_chat_fullname.text = contact.fullname
        } else {
            mToolbarChat.toolbar_chat_fullname.text = mReceivedContact.fullname
        }
        mToolbarChat.toolbar_chat_avatar.downloadAndSetImage(mReceivedContact.avatarUrl)
        mToolbarChat.toolbar_chat_status.text = mReceivedContact.state
    }

    override fun onPause() {
        super.onPause()
        mToolbarChat.visibility = View.GONE
        mRefContact.removeEventListener(mListenerChatToolbar)
    }
}