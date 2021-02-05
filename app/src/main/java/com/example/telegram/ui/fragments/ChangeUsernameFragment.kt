package com.example.telegram.ui.fragments

import com.example.telegram.R
import com.example.telegram.utils.*
import kotlinx.android.synthetic.main.fragment_change_username.*
import java.util.*

class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {

    private lateinit var mNewUsername: String

    override fun onResume() {
        super.onResume()
        settings_change_username_et.setText(USER.username)
    }

    override fun applyChanges() {
        mNewUsername = settings_change_username_et.text.toString().toLowerCase(Locale.getDefault())
        if (mNewUsername.isEmpty()) {
            showToast(getString(R.string.change_username_warning))
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(mNewUsername)) {
                        showToast(getString(R.string.username_already_exist_warning))
                    } else {
                        changeUsername()
                    }
                })
        }
    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUsername).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername()
                }
            }
    }

    private fun updateCurrentUsername() {
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(USER_USERNAME).setValue(mNewUsername)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    deletePreviousUsername()
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }

    private fun deletePreviousUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast(getString(R.string.toast_username_updated))
                    fragmentManager?.popBackStack()
                    USER.username = mNewUsername
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }
}