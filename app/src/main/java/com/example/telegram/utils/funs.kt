package com.example.telegram.utils

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.telegram.R
import com.example.telegram.activities.RegisterActivity
import com.example.telegram.ui.fragments.ChatsFragment

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.changeActivity(activity: AppCompatActivity) {
    val intent = Intent(this, activity::class.java)
    startActivity(intent)
    this.finish()
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, addToStack: Boolean = true) {
    if (addToStack) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.dataContainer, fragment)
            .addToBackStack(fragment.tag)
            .commit()
    } else {
        supportFragmentManager.beginTransaction()
            .replace(R.id.dataContainer, fragment)
            .commit()
    }
}

fun Fragment.replaceFragment(fragment: Fragment) {
    this.fragmentManager?.beginTransaction()
        ?.replace(R.id.dataContainer, fragment)
        ?.addToBackStack(fragment.tag)
        ?.commit()
}