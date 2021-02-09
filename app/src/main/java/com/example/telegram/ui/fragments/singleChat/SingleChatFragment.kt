package com.example.telegram.ui.fragments.singleChat

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.models.UserModel
import com.example.telegram.ui.fragments.BaseFragment
import com.example.telegram.utils.*
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
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
    private lateinit var mLayoutManager: LinearLayoutManager
    private var mMessagesCount = 20
    private var mIsScroll = false
    private var mIsScrollToEnd = true

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initSendMessage()
        initMessagesList()
    }

    private fun initFields() {
        mLayoutManager = LinearLayoutManager(this.context)
        chat_message_input.addTextChangedListener(AppTextWatcher {
            val msg = chat_message_input.text.toString()
            if (msg.isEmpty()) {
                send_attach_btn.visibility = View.VISIBLE
                send_message_btn.visibility = View.GONE
            } else {
                send_attach_btn.visibility = View.GONE
                send_message_btn.visibility = View.VISIBLE
            }
        })

        send_attach_btn.setOnClickListener { sendAtachment() }
    }

    private fun sendAtachment() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .start(APP_ACTIVITY, this)
    }

    private fun initMessagesList() {
        mRecyclerView = chat_messages
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)

        mMessagesListener = AppChildEventListener {
            val message = it.getCommonModel()
            if (mIsScrollToEnd) {
                mAdapter.addItemToBottom(message) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(message) {}
            }
        }

        mRefMessages.limitToLast(mMessagesCount).addChildEventListener(mMessagesListener)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScroll = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var firstVisiblePosition = mLayoutManager.findFirstVisibleItemPosition()
                if (mIsScroll && dy < 0 && firstVisiblePosition <= 5) {
                    updateData()
                }
            }
        })
    }

    private fun updateData() {
        mIsScrollToEnd = false
        mIsScroll = false
        mMessagesCount += 20
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mMessagesCount).addChildEventListener(mMessagesListener)
    }

    private fun initSendMessage() {
        send_message_btn.setOnClickListener {
            mIsScrollToEnd = true
            var message = chat_message_input.text.toString()
            if (message.isEmpty() || message.isBlank()) {
                showToast(getString(R.string.chat_message_warning))
            } else {
                message = message.trim()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val messageKey =
                REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(contact.id)
                    .push().key.toString()
            val path = REF_STORAGE_ROOT.child(FOLDER_MESSAGE_IMAGE).child(messageKey)

            putImageToStorage(uri, path) {
                getUrlFromStorage(path) {
                    saveImageToDatabase(contact.id, it, messageKey)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mToolbarChat.visibility = View.GONE
        mRefContact.removeEventListener(mListenerChatToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
        hideKeyboard()
    }
}