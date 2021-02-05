package com.example.telegram.ui.fragments

import com.example.telegram.R
import com.example.telegram.utils.*
import kotlinx.android.synthetic.main.fragment_change_bio.*

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    override fun onResume() {
        super.onResume()
        settings_change_bio_et.setText(USER.bio)
    }

    override fun applyChanges() {
        val newBio = settings_change_bio_et.text.toString()
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(USER_BIO).setValue(newBio)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast(getString(R.string.users_bio_updated))
                    USER.bio = newBio
                    fragmentManager?.popBackStack()
                }
            }
    }
}