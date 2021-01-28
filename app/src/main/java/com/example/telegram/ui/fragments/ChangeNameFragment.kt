package com.example.telegram.ui.fragments

import android.view.*
import androidx.fragment.app.Fragment
import com.example.telegram.MainActivity
import com.example.telegram.R
import com.example.telegram.utils.*
import kotlinx.android.synthetic.main.fragment_change_name.*

class ChangeNameFragment : Fragment(R.layout.fragment_change_name) {
    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

    private fun initFields() {
        val fullnameList = USER.fullname.split(" ")
        settings_change_name_et.setText(fullnameList[0])
        settings_change_surname_et.setText(fullnameList[1])
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as MainActivity).menuInflater.inflate(R.menu.settings_change_name_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting_apply -> applyChanges()
        }
        return true
    }

    private fun applyChanges() {
        val name = settings_change_name_et.text.toString()
        val surname = settings_change_surname_et.text.toString()

        if (name.isEmpty()) {
            showToast(getString(R.string.change_name_warning))
        } else {
            val fullname = "$name $surname"
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(USER_FULLNAME).setValue(fullname)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast(getString(R.string.toast_name_updated))
                        USER.fullname = fullname
                        fragmentManager?.popBackStack()
                    }
                }
        }
    }
}