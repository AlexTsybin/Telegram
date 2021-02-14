package com.example.telegram.ui.views.group

import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.views.base.BaseFragment
import com.example.telegram.utils.APP_ACTIVITY
import com.example.telegram.utils.AppValueEventListener
import com.example.telegram.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.fragment_create_group.*

class CreateGroupFragment(private var contactsList: List<CommonModel>) :
    BaseFragment(R.layout.fragment_create_group) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapterCreate: CreateGroupAdapter

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.create_group)
        hideKeyboard()
        create_group_fab.setOnClickListener { }
        create_group_input_name.requestFocus()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = create_group_contacts_rv
        mAdapterCreate = CreateGroupAdapter()

        mRecyclerView.adapter = mAdapterCreate
        contactsList.forEach {
            mAdapterCreate.updateContactsList(it)
        }
    }
}