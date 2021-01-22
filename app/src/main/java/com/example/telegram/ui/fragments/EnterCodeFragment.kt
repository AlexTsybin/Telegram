package com.example.telegram.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.telegram.R
import com.example.telegram.utils.AppTextWatcher
import com.example.telegram.utils.showToast
import kotlinx.android.synthetic.main.fragment_enter_code.*

class EnterCodeFragment : Fragment(R.layout.fragment_enter_code) {
    override fun onStart() {
        super.onStart()
        register_code_input.addTextChangedListener(AppTextWatcher {
            val string = register_code_input.text.toString()
            if (string.length == 6) {
                verifyCode()
            }
        })
    }

    private fun verifyCode() {
        showToast(getString(R.string.code_verified))
    }
}