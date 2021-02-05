package com.example.telegram.ui.fragments

import com.example.telegram.R
import com.example.telegram.utils.*
import kotlinx.android.synthetic.main.fragment_change_name.*

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    override fun onResume() {
        super.onResume()
        initFields()
    }

    private fun initFields() {
        val fullnameList = USER.fullname.split(" ")
        if (fullnameList.size > 1) {
            settings_change_name_et.setText(fullnameList[0])
            settings_change_surname_et.setText(fullnameList[1])
        } else {
            settings_change_name_et.setText(fullnameList[0])
        }
    }

    override fun applyChanges() {
        val name = settings_change_name_et.text.toString()
        val surname = settings_change_surname_et.text.toString()

        if (name.isEmpty()) {
            showToast(getString(R.string.change_name_warning))
        } else {
            val fullname = "$name $surname"
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(USER_FULLNAME).setValue(fullname)
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