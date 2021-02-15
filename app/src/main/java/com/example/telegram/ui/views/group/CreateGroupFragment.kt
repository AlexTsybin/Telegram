package com.example.telegram.ui.views.group

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.views.base.BaseFragment
import com.example.telegram.ui.views.chats.ChatsFragment
import com.example.telegram.utils.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.fragment_create_group.*
import kotlinx.android.synthetic.main.fragment_settings.*

class CreateGroupFragment(private var contactsList: List<CommonModel>) :
    BaseFragment(R.layout.fragment_create_group) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapterCreate: CreateGroupAdapter
    private var mUri = Uri.EMPTY

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.create_group)
        hideKeyboard()
        create_group_avatar.setOnClickListener {
            addGroupAvatar()
        }
        create_group_fab.setOnClickListener {
            val groupName = create_group_input_name.text.toString()
            if (groupName.isEmpty() || groupName.isBlank()) {
                showToast(getString(R.string.group_name_empty_wrn))
            } else {
                saveGroupToDB(groupName, mUri, contactsList) {
                    replaceFragment(ChatsFragment())
                }
            }
        }
        create_group_input_name.requestFocus()
        initRecyclerView()
        create_group_count.text = getMembersCount(contactsList.size)
    }

    private fun addGroupAvatar() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecyclerView() {
        mRecyclerView = create_group_contacts_rv
        mAdapterCreate = CreateGroupAdapter()

        mRecyclerView.adapter = mAdapterCreate
        contactsList.forEach {
            mAdapterCreate.updateContactsList(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            mUri = CropImage.getActivityResult(data).uri
            create_group_avatar.setImageURI(mUri)
        }
    }
}