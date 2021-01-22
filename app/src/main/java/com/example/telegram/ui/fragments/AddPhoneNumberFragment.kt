package com.example.telegram.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.telegram.R
import kotlinx.android.synthetic.main.fragment_add_phone_number.*

class AddPhoneNumberFragment : Fragment(R.layout.fragment_add_phone_number) {
    override fun onStart() {
        super.onStart()
        register_phone_send.setOnClickListener { sendCode() }
    }

    private fun sendCode() {
        if (register_phone_input.text.toString().isEmpty()) {
            Toast.makeText(activity, getString(R.string.enter_phone_number), Toast.LENGTH_SHORT)
                .show()
        } else {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.registerDataContainer, EnterCodeFragment())
                ?.addToBackStack(EnterCodeFragment().tag)
                ?.commit()
        }
    }
}

