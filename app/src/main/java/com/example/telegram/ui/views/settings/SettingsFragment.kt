package com.example.telegram.ui.views.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.ui.views.base.BaseFragment
import com.example.telegram.utils.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.settings)
        setHasOptionsMenu(true)
        initFields()
    }

    private fun initFields() {
        settings_fullname.text = USER.fullname
        settings_user_status.text = USER.state
        settings_user_phone_number.text = USER.phone
        settings_username.text = USER.username
        settings_user_bio.text = USER.bio
        setting_username_block.setOnClickListener { replaceFragment(ChangeUsernameFragment()) }
        setting_bio_block.setOnClickListener { replaceFragment(ChangeBioFragment()) }
        settings_change_avatar.setOnClickListener { changeUserAvatar() }
        settings_user_avatar.downloadAndSetImage(USER.avatarUrl)
    }

    private fun changeUserAvatar() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting_log_out -> logOut()
            R.id.setting_edit_name -> editName()
        }
        return true
    }

    private fun editName() {
        replaceFragment(ChangeNameFragment())
    }

    private fun logOut() {
        AppStates.updateState(AppStates.OFFLINE)
        AUTH.signOut()
        restartActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_USER_AVATAR).child(CURRENT_UID)

            putFileToStorage(uri, path) {
                getUrlFromStorage(path) {
                    saveUrlToDatabase(it) {
                        settings_user_avatar.downloadAndSetImage(it)
                        showToast(getString(R.string.data_updated))
                        USER.avatarUrl = it
                        APP_ACTIVITY.mAppDrawer.updateHeader()
                    }
                }
            }
        }
    }
}