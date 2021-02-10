package com.example.telegram.utils

import android.net.Uri
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference

lateinit var CURRENT_UID: String
lateinit var USER: UserModel

const val FOLDER_USER_AVATAR = "user_avatar"
const val FOLDER_MESSAGE_IMAGE = "message_image"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_CONTACTS = "contacts"
const val NODE_MESSAGES = "messages"

const val USER_ID = "id"
const val USER_PHONE = "phone"
const val USER_USERNAME = "username"
const val USER_FULLNAME = "fullname"
const val USER_BIO = "bio"
const val USER_AVATAR_URL = "avatarUrl"
const val USER_STATE = "state"

// Chat fields
const val MESSAGE_TEXT = "messageText"
const val MESSAGE_TYPE = "messageType"
const val MESSAGE_SENDER = "messageSender"
const val MESSAGE_TIMESTAMP = "timeStamp"
const val MESSAGE_FILE_URL = "fileUrl"

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference

    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
}

inline fun saveUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .child(USER_AVATAR_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.username.isEmpty()) {
                USER.username = CURRENT_UID
            }
            function()
        })
}

fun addContactsToDatabase(arrayContacts: ArrayList<CommonModel>) {
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener {
            it.children.forEach { snapshot ->
                arrayContacts.forEach { contact ->
                    if (snapshot.key == contact.phone) {
                        REF_DATABASE_ROOT.child(NODE_CONTACTS).child(CURRENT_UID)
                            .child(snapshot.value.toString()).child(USER_ID)
                            .setValue(snapshot.value.toString())
                            .addOnFailureListener { showToast(it.message.toString()) }

                        REF_DATABASE_ROOT.child(NODE_CONTACTS).child(CURRENT_UID)
                            .child(snapshot.value.toString()).child(USER_FULLNAME)
                            .setValue(contact.fullname)
                            .addOnFailureListener { showToast(it.message.toString()) }
                    }
                }
            }
        })
    }
}

// Function transforms data from Firebase to CommonModel
fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

// Function transforms data from Firebase to UserModel
fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()

fun sendMessage(message: String, contactId: String, messageTypeText: String, function: () -> Unit) {
    val refCurrentUser = "$NODE_MESSAGES/$CURRENT_UID/$contactId"
    val refContactUser = "$NODE_MESSAGES/$contactId/$CURRENT_UID"

    val messageKey = REF_DATABASE_ROOT.child(refCurrentUser).push().key
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[USER_ID] = messageKey.toString()
    mapMessage[MESSAGE_SENDER] = CURRENT_UID
    mapMessage[MESSAGE_TEXT] = message
    mapMessage[MESSAGE_TYPE] = messageTypeText
    mapMessage[MESSAGE_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refCurrentUser/$messageKey"] = mapMessage
    mapDialog["$refContactUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun setBioToDatabase(newBio: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(USER_BIO)
        .setValue(newBio)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                showToast(APP_ACTIVITY.getString(R.string.users_bio_updated))
                USER.bio = newBio
                APP_ACTIVITY.supportFragmentManager.popBackStack()
            }
        }
}

fun setNameToDatabase(fullname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(USER_FULLNAME)
        .setValue(fullname)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                showToast(APP_ACTIVITY.getString(R.string.toast_name_updated))
                USER.fullname = fullname
                APP_ACTIVITY.mAppDrawer.updateHeader()
                APP_ACTIVITY.supportFragmentManager.popBackStack()
            }
        }
}

fun updateCurrentUsername(newUsername: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(USER_USERNAME)
        .setValue(newUsername)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                deletePreviousUsername(newUsername)
            } else {
                showToast(it.exception?.message.toString())
            }
        }
}

private fun deletePreviousUsername(newUsername: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnCompleteListener {
            if (it.isSuccessful) {
                showToast(APP_ACTIVITY.getString(R.string.toast_username_updated))
                APP_ACTIVITY.supportFragmentManager.popBackStack()
                USER.username = newUsername
            } else {
                showToast(it.exception?.message.toString())
            }
        }
}

fun saveImageToDatabase(contactId: String, imageUrl: String, messageKey: String) {
    val refCurrentUser = "$NODE_MESSAGES/$CURRENT_UID/$contactId"
    val refContactUser = "$NODE_MESSAGES/$contactId/$CURRENT_UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[USER_ID] = messageKey
    mapMessage[MESSAGE_SENDER] = CURRENT_UID
    mapMessage[MESSAGE_TYPE] = MESSAGE_TYPE_IMAGE
    mapMessage[MESSAGE_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[MESSAGE_FILE_URL] = imageUrl

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refCurrentUser/$messageKey"] = mapMessage
    mapDialog["$refContactUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getMessageKey(contactId: String) =
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(contactId)
        .push().key.toString()

fun uploadFileToStorage(uri: Uri, messageKey: String) {
    showToast("Record OK")
}