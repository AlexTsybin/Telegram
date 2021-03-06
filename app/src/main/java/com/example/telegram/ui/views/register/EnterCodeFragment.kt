package com.example.telegram.ui.views.register

import androidx.fragment.app.Fragment
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.utils.*
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_code.*

class EnterCodeFragment(val phoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.title = phoneNumber
        register_code_input.addTextChangedListener(AppTextWatcher {
            val string = register_code_input.text.toString()
            if (string.length == 6) {
                enterCode()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    private fun enterCode() {
        val verifyCode = register_code_input.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, verifyCode)
        AUTH.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val userMap = mutableMapOf<String, Any>()
                userMap[USER_ID] = uid
                userMap[USER_PHONE] = phoneNumber

                REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        if (!it.hasChild(USER_USERNAME)) {
                            userMap[USER_USERNAME] = uid
                        }

                        REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber).setValue(uid)
                            .addOnFailureListener { showToast(it.message.toString()) }
                            .addOnSuccessListener {
                                REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                                    .updateChildren(userMap)
                                    .addOnFailureListener { showToast(it.message.toString()) }
                                    .addOnSuccessListener {
                                        showToast("Welcome to Telegram!")
                                        restartActivity()
                                    }
                            }
                    })
            } else {
                showToast(it.exception?.message.toString())
            }
        }
    }
}