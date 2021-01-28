package com.example.telegram.ui.fragments

import androidx.fragment.app.Fragment
import com.example.telegram.MainActivity
import com.example.telegram.R
import com.example.telegram.activities.RegisterActivity
import com.example.telegram.utils.AUTH
import com.example.telegram.utils.changeActivity
import com.example.telegram.utils.replaceFragment
import com.example.telegram.utils.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
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
                        (activity as RegisterActivity).changeActivity(MainActivity())
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
            activity as RegisterActivity,
            mVerifyCallback
        )
    }
}

