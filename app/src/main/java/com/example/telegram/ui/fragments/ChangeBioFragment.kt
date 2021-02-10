package com.example.telegram.ui.fragments

import com.example.telegram.R
import com.example.telegram.database.USER
import com.example.telegram.database.setBioToDatabase
import com.example.telegram.utils.*
import kotlinx.android.synthetic.main.fragment_change_bio.*

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    override fun onResume() {
        super.onResume()
        settings_change_bio_et.setText(USER.bio)
    }

    override fun applyChanges() {
        val newBio = settings_change_bio_et.text.toString()
        setBioToDatabase(newBio)
    }
}