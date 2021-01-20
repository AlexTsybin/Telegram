package com.example.telegram.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import com.example.telegram.R

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}