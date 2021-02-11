package com.example.telegram.ui.views

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.example.telegram.R
import com.example.telegram.utils.APP_ACTIVITY
import com.example.telegram.utils.hideKeyboard

open class BaseChangeFragment(layout: Int) : BaseFragment(layout) {

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        APP_ACTIVITY.mAppDrawer.disableDrawer()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        APP_ACTIVITY.menuInflater.inflate(R.menu.settings_change_name_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting_apply -> applyChanges()
        }
        return true
    }

    open fun applyChanges() {

    }
}