package com.example.telegram.ui.views.register

import androidx.fragment.app.Fragment
import com.example.telegram.R
import com.example.telegram.database.AUTH
import com.example.telegram.utils.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_add_phone_number.*
import java.util.concurrent.TimeUnit

class AddPhoneNumberFragment : Fragment(R.layout.fragment_add_phone_number) {

    private lateinit var mPhoneNumber: String
    private lateinit var mVerifyCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()
        register_phone_send.setOnClickListener { sendCode() }
        mVerifyCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast("Welcome to Telegram!")
                        restartActivity()
                    } else {
                        showToast(it.exception?.message.toString())
                    }
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(EnterCodeFragment(mPhoneNumber, id))
            }
        }
    }

    private fun sendCode() {
        if (register_phone_input.text.toString().isEmpty()) {
            showToast(getString(R.string.enter_phone_number))
        } else {
            //replaceFragment(EnterCodeFragment())
            authUser()
        }
    }

    private fun authUser() {
        mPhoneNumber = register_phone_input.text.toString()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mPhoneNumber,
            60,
            TimeUnit.SECONDS,
            APP_ACTIVITY,
            mVerifyCallback
        )
    }
}

