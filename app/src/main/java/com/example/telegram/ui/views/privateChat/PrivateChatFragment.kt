package com.example.telegram.ui.views.privateChat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.models.CommonModel
import com.example.telegram.models.UserModel
import com.example.telegram.ui.views.base.BaseFragment
import com.example.telegram.ui.views.chats.ChatsFragment
import com.example.telegram.ui.views.privateChat.views.AppViewFactory
import com.example.telegram.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.chat_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_chat.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrivateChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var mListenerChatToolbar: AppValueEventListener
    private lateinit var mReceivedContact: UserModel
    private lateinit var mToolbarChat: View
    private lateinit var mRefContact: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: PrivateChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

    private var mMessagesCount = 20
    private var mIsScroll = false
    private var mIsScrollToEnd = true

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initSendMessage()
        initRecyclerView()
    }

    private fun initFields() {
        setHasOptionsMenu(true)
        mLayoutManager = LinearLayoutManager(this.context)
        mAppVoiceRecorder = AppVoiceRecorder()
        mBottomSheetBehavior = BottomSheetBehavior.from(chat_bottom_sheet)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        chat_message_input.addTextChangedListener(AppTextWatcher {
            val msg = chat_message_input.text.toString()
            if (msg.isEmpty() || msg.equals("Voice message is recording")) {
                attach_btn_block.visibility = View.VISIBLE
                send_message_btn.visibility = View.GONE
            } else {
                attach_btn_block.visibility = View.GONE
                send_message_btn.visibility = View.VISIBLE
            }
        })

        send_attach_btn.setOnClickListener { sendAtachment() }

        CoroutineScope(Dispatchers.IO).launch {
            send_voice_btn.setOnTouchListener { view, motionEvent ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                        // TODO start record
                        chat_message_input.setText(getString(R.string.voice_message_recording_indicator))
                        send_voice_btn.setColorFilter(
                            ContextCompat.getColor(
                                APP_ACTIVITY,
                                R.color.colorPrimary
                            )
                        )
                        val messageKey = getMessageKey(contact.id)
                        mAppVoiceRecorder.startRecord(messageKey)
                    } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                        // TODO stop record
                        chat_message_input.setText("")
                        send_voice_btn.setColorFilter(
                            ContextCompat.getColor(
                                APP_ACTIVITY,
                                R.color.middle_text_color
                            )
                        )
                        mAppVoiceRecorder.stopRecord { file, messageKey ->
                            uploadFileToStorage(
                                Uri.fromFile(file),
                                messageKey,
                                contact.id,
                                MESSAGE_TYPE_VOICE
                            )
                            mIsScrollToEnd = true
                        }
                    }
                }
                true
            }
        }
    }

    private fun sendAtachment() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        chat_attach_image.setOnClickListener { sendImageAttachment() }
        chat_attach_file.setOnClickListener { sendFileAttachment() }
    }

    private fun sendImageAttachment() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .start(APP_ACTIVITY, this)
    }

    private fun sendFileAttachment() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun initRecyclerView() {
        mRecyclerView = chat_messages
        mAdapter = PrivateChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager

        mMessagesListener = AppChildEventListener {
            val message = it.getCommonModel()
            if (mIsScrollToEnd) {
                mAdapter.addItemToBottom(AppViewFactory.getView(message)) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(AppViewFactory.getView(message)) {}
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
                val firstVisiblePosition = mLayoutManager.findFirstVisibleItemPosition()
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
                    addChatToMainList(contact.id, TYPE_PRIVATE_CHAT)
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

        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri
                    val messageKey = getMessageKey(contact.id)
                    uploadFileToStorage(uri, messageKey, contact.id, MESSAGE_TYPE_IMAGE)
                    mIsScrollToEnd = true
                }
                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    val messageKey = getMessageKey(contact.id)
                    val fileName: String = uri?.let { getFileNameFromUri(it) }.toString()
                    uri?.let {
                        uploadFileToStorage(
                            it,
                            messageKey,
                            contact.id,
                            MESSAGE_TYPE_FILE,
                            fileName
                        )
                    }
                    mIsScrollToEnd = true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.private_chat_options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_chat_clear -> clearChat(contact.id) {
                showToast(getString(R.string.chat_cleared))
                replaceFragment(ChatsFragment())
            }
            R.id.menu_chat_delete -> deleteChat(contact.id) {
                showToast(getString(R.string.chat_deleted))
                replaceFragment(ChatsFragment())
            }
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        mToolbarChat.visibility = View.GONE
        mRefContact.removeEventListener(mListenerChatToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
        hideKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecorder()
        mAdapter.onDestroy()
    }
}