 package com.example.telegram.ui.fragments

import androidx.fragment.app.Fragment
import com.example.telegram.R
import com.example.telegram.utils.APP_ACTIVITY
import com.example.telegram.utils.hideKeyboard

 /**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment(R.layout.fragment_chats) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.app_name)
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
    }
}