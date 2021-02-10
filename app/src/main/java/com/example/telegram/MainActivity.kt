package com.example.telegram

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.telegram.database.AUTH
import com.example.telegram.database.initFirebase
import com.example.telegram.database.initUser
import com.example.telegram.databinding.ActivityMainBinding
import com.example.telegram.ui.fragments.MainFragment
import com.example.telegram.ui.fragments.register.AddPhoneNumberFragment
import com.example.telegram.ui.objects.AppDrawer
import com.example.telegram.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer
    lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser {
            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
            initFields()
            initFunc()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onPause() {
        super.onPause()
        AppStates.updateState(AppStates.OFFLINE)
    }

    override fun onStop() {
        super.onStop()
    }

    private fun initFunc() {
        setSupportActionBar(mToolbar)
        if (AUTH.currentUser == null) {
            replaceFragment(AddPhoneNumberFragment(), false)
        } else {
            mAppDrawer.create()
            replaceFragment(MainFragment(), false)
        }
    }

    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(
                APP_ACTIVITY,
                READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initContacts()
        }
    }
}