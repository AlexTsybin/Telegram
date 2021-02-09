package com.example.telegram.ui.fragments.singleChat

import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.models.UserModel
import com.example.telegram.ui.fragments.BaseFragment
import com.example.telegram.utils.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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
    private lateinit var mMessagesListener: AppChildEventListener
    private var mMessagesCount = 20
    private var mIsScroll = false
    private var mIsScrollToEnd = true
    private var mListenersList = mutableListOf<AppChildEventListener>()

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

        mMessagesListener = AppChildEventListener {
            mAdapter.addItem(it.getCommonModel())
            if (mIsScrollToEnd){
                mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
            }
        }

        mRefMessages.limitToLast(mMessagesCount).addChildEventListener(mMessagesListener)
        mListenersList.add(mMessagesListener)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    mIsScroll = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScroll && dy < 0){
                    updateData()
                }
            }
        })
    }

    private fun updateData() {
        mIsScrollToEnd = false
        mIsScroll = false
        mMessagesCount += 20
        mRefMessages.limitToLast(mMessagesCount).addChildEventListener(mMessagesListener)
        mListenersList.add(mMessagesListener)
    }

    private fun initSendMessage() {
        send_message_image.setOnClickListener {
            mIsScrollToEnd = true
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
        mListenersList.forEach {
            mRefMessages.removeEventListener(it)
        }
        hideKeyboard()
    }
}