package com.example.telegram.ui.views.group

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.views.base.BaseFragment
import com.example.telegram.utils.*
import kotlinx.android.synthetic.main.fragment_add_contact.*
import kotlinx.android.synthetic.main.fragment_chats.*

class AddContactFragment : BaseFragment(R.layout.fragment_add_contact) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactAdapter
    private val mRefContactsList = REF_DATABASE_ROOT.child(NODE_CONTACTS).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mChatsItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.create_group)
        hideKeyboard()
        initRecyclerView()
        checkedContactsList.clear()
        add_contact_fab.setOnClickListener {
            if (checkedContactsList.isEmpty()){
                showToast("Add contact to create group")
            } else {
                replaceFragment(CreateGroupFragment(checkedContactsList))
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = add_contact_rv
        mAdapter = AddContactAdapter()

        // 1st query
        mRefContactsList.addListenerForSingleValueEvent(AppValueEventListener {
            mChatsItems = it.children.map { it.getCommonModel() }
            mChatsItems.forEach { model ->

                // 2nd query
                mRefUsers.child(model.id).addListenerForSingleValueEvent(AppValueEventListener {
                    val newModel = it.getCommonModel()

                    // 3rd query
                    mRefMessages.child(model.id).limitToLast(1)
                        .addListenerForSingleValueEvent(AppValueEventListener {
                            val tempList = it.children.map { it.getCommonModel() }
                            if (tempList.isEmpty()) {
                                newModel.lastMessage = getString(R.string.no_messages)
                            } else {
                                newModel.lastMessage = tempList[0].messageText
                            }

                            if (newModel.fullname.isEmpty() || newModel.fullname.isBlank()) {
                                newModel.fullname = newModel.phone
                            }
                            mAdapter.updateChatsList(newModel)
                        })
                })
            }
        })

        mRecyclerView.adapter = mAdapter
    }

    companion object {
        val checkedContactsList = mutableListOf<CommonModel>()
    }
}