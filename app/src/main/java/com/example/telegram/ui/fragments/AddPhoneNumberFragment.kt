package com.example.telegram.ui.fragments

import androidx.fragment.app.Fragment
import com.example.telegram.R
import com.example.telegram.utils.replaceFragment
import com.example.telegram.utils.showToast
import kotlinx.android.synthetic.main.fragment_add_phone_number.*

class AddPhoneNumberFragment : Fragment(R.layout.fragment_add_phone_number) {
    override fun onStart() {
        super.onStart()
        register_phone_send.setOnClickListener { sendCode() }
    }

    private fun sendCode() {
        if (register_phone_input.text.toString().isEmpty()) {
            showToast(getString(R.string.enter_phone_number))
        } else {
            replaceFragment(EnterCodeFragment())
        }
    }
}

