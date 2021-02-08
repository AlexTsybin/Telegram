package com.example.telegram.ui.fragments.singleChat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.models.UserModel
import com.example.telegram.ui.fragments.BaseFragment
import com.example.telegram.utils.*
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_chat.view.*

class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var mListenerChatToolbar: AppValueEventListener
    private lateinit var mReceivedContact: UserModel
    private lateinit var mToolbarChat: View
    private lateinit var mRefContact: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppValueEventListener
    private var mMessagesList = emptyList<CommonModel>()

    override fun onResume() {
        super.onResume()
        initToolbar()
        initSendMessage()
        initMessagesList()
    }

    private fun initMessagesList() {
        mRecyclerView = chat_messages
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mMessagesListener = AppValueEventListener { dataSnapshot ->
            mMessagesList = dataSnapshot.children.map {
                it.getCommonModel()
            }
            mAdapter.setList(mMessagesList)
            mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
        }
        mRefMessages.addValueEventListener(mMessagesListener)
    }

    private fun initSendMessage() {
        send_message_image.setOnClickListener {
            val message = chat_message_input.text.toString()
            if (message.isEmpty()) {
                showToast(getString(R.string.chat_message_warning))
            } else {
                sendMessage(message, contact.id, MESSAGE_TYPE_TEXT) {
                    chat_message_input.setText("")
                }
            }
        }
    }

    private fun initToolbar() {
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
        if (mReceivedContact.fullname.isEmpty()) {
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
        mRefMessages.removeEventListener(mMessagesListener)
        hideKeyboard()
    }
}