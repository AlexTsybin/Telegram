package com.example.telegram.ui.views.base

import androidx.fragment.app.Fragment
import com.example.telegram.utils.APP_ACTIVITY

/**
 * A simple [Fragment] subclass.
 */
open class BaseFragment(layout: Int) : Fragment(layout) {
    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.disableDrawer()
    }
}