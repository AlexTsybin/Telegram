package com.example.telegram.ui.views.group

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.models.CommonModel
import com.example.telegram.utils.APP_ACTIVITY
import com.example.telegram.utils.AppValueEventListener
import com.example.telegram.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_chats.*

/**
 * A simple [Fragment] subclass.
 */
class GroupFragment : Fragment(R.layout.fragment_chats) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: GroupAdapter
    private val mRefChatsList = REF_DATABASE_ROOT.child(NODE_CHATS_LIST).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mChatsItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.app_name)
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()

        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = chats_rv
        mAdapter = GroupAdapter()

        // 1st query
        mRefChatsList.addListenerForSingleValueEvent(AppValueEventListener {
            mChatsItems = it.children.map { it.getCommonModel() }
            mChatsItems.forEach { model ->

                // 2nd query
                mRefUsers.child(model.id).addListenerForSingleValueEvent(AppValueEventListener {
                    val newModel = it.getCommonModel()

                    // 3rd query
                    mRefMessages.child(model.id).limitToLast(1).addListenerForSingleValueEvent(AppValueEventListener{
                        val tempList = it.children.map { it.getCommonModel() }
                        if (tempList.isEmpty()){
                            newModel.lastMessage = getString(R.string.no_messages)
                        } else {
                            newModel.lastMessage = tempList[0].messageText
                        }

                        if (newModel.fullname.isEmpty() || newModel.fullname.isBlank()){
                            newModel.fullname = newModel.phone
                        }
                        mAdapter.updateChatsList(newModel)
                    })
                })
            }
        })

        mRecyclerView.adapter = mAdapter
    }
}