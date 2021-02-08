package com.example.telegram.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.telegram.R
import com.example.telegram.databinding.ActivityRegisterBinding
import com.example.telegram.ui.fragments.AddPhoneNumberFragment
import com.example.telegram.utils.initFirebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initFirebase()
    }

    override fun onStart() {
        super.onStart()
        mToolbar = mBinding.registerToolbar
        setSupportActionBar(mToolbar)
        title = getString(R.string.title_your_phone)
        supportFragmentManager.beginTransaction()
            .add(R.id.data_container, AddPhoneNumberFragment())
            .commit()
    }
}