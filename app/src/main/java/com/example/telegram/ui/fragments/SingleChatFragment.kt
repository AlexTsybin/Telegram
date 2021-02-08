package com.example.telegram.ui.fragments

import android.view.View
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.utils.APP_ACTIVITY
import kotlinx.android.synthetic.main.activity_main.view.*

class SingleChatFragment(contact: CommonModel) : BaseFragment(R.layout.fragment_single_chat) {
    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mToolbar.toolbar_chat.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        APP_ACTIVITY.mToolbar.toolbar_chat.visibility = View.GONE
    }
}