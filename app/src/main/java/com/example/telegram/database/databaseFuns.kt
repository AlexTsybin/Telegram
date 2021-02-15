package com.example.telegram.database

import android.net.Uri
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.models.UserModel
import com.example.telegram.utils.APP_ACTIVITY
import com.example.telegram.utils.AppValueEventListener
import com.example.telegram.utils.TYPE_GROUP_CHAT
import com.example.telegram.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.net.URI

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

inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
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
    mapMessage[CHILD_TYPE] = messageTypeText
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

fun saveFileToDatabase(
    contactId: String,
    fileUrl: String,
    messageKey: String,
    messageType: String,
    fileName: String
) {
    val refCurrentUser = "$NODE_MESSAGES/$CURRENT_UID/$contactId"
    val refContactUser = "$NODE_MESSAGES/$contactId/$CURRENT_UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[USER_ID] = messageKey
    mapMessage[MESSAGE_SENDER] = CURRENT_UID
    mapMessage[CHILD_TYPE] = messageType
    mapMessage[MESSAGE_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[MESSAGE_FILE_URL] = fileUrl
    mapMessage[MESSAGE_TEXT] = fileName

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refCurrentUser/$messageKey"] = mapMessage
    mapDialog["$refContactUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getMessageKey(contactId: String) =
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(contactId)
        .push().key.toString()

fun uploadFileToStorage(
    uri: Uri,
    messageKey: String,
    contactId: String,
    messageType: String,
    fileName: String = ""
) {
    val path = REF_STORAGE_ROOT.child(FOLDER_FILES).child(messageKey)

    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            saveFileToDatabase(contactId, it, messageKey, messageType, fileName)
        }
    }
}

fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(mFile)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun addChatToMainList(id: String, chatType: String) {
    val refCurrent = "$NODE_CHATS_LIST/$CURRENT_UID/$id"
    val refContact = "$NODE_CHATS_LIST/$id/$CURRENT_UID"

    val mapCurrent = hashMapOf<String, Any>()
    val mapContact = hashMapOf<String, Any>()

    mapCurrent[USER_ID] = id
    mapCurrent[CHILD_TYPE] = chatType

    mapContact[USER_ID] = CURRENT_UID
    mapContact[CHILD_TYPE] = chatType

    val commonMap = hashMapOf<String, Any>()
    commonMap[refCurrent] = mapCurrent
    commonMap[refContact] = mapContact

    REF_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun clearChat(contactId: String, function: () -> Unit) {
    // Clear current user messages
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(contactId)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {
            // Clear contact's messages
            REF_DATABASE_ROOT.child(NODE_MESSAGES).child(contactId).child(CURRENT_UID)
                .removeValue()
                .addOnFailureListener { showToast(it.message.toString()) }
                .addOnSuccessListener { function() }
        }
}

fun deleteChat(contactId: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_CHATS_LIST).child(CURRENT_UID).child(contactId)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }
}

fun saveGroupToDB(
    groupName: String,
    uri: Uri,
    contactsList: List<CommonModel>,
    function: () -> Unit
) {
    val groupKey = REF_DATABASE_ROOT.child(NODE_GROUPS).push().key.toString()
    val path = REF_DATABASE_ROOT.child(NODE_GROUPS).child(groupKey)

    val groupDataMap = hashMapOf<String, Any>()
    groupDataMap[USER_ID] = groupKey
    groupDataMap[USER_FULLNAME] = groupName
    groupDataMap[USER_AVATAR_URL] = "empty"

    val membersMap = hashMapOf<String, Any>()
    contactsList.forEach {
        membersMap[it.id] = USER_MEMBER
    }
    membersMap[CURRENT_UID] = USER_CREATOR

    groupDataMap[NODE_MEMBERS] = membersMap

    path.updateChildren(groupDataMap)
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {
            if (uri != Uri.EMPTY) {
                val storagePath = REF_STORAGE_ROOT.child(FOLDER_GROUPS_AVATARS).child(groupKey)
                putFileToStorage(uri, storagePath) {
                    getUrlFromStorage(storagePath) {
                        path.child(USER_AVATAR_URL).setValue(it)

                        addGroupToChatsList(groupDataMap, contactsList) {
                            function()
                        }
                    }
                }
            } else {
                addGroupToChatsList(groupDataMap, contactsList) {
                    function()
                }
            }
        }
}

fun addGroupToChatsList(
    groupDataMap: HashMap<String, Any>,
    contactsList: List<CommonModel>,
    function: () -> Unit
) {
    val path = REF_DATABASE_ROOT.child(NODE_CHATS_LIST)
    val map = hashMapOf<String, Any>()

    map[USER_ID] = groupDataMap[USER_ID].toString()
    map[CHILD_TYPE] = TYPE_GROUP_CHAT
    contactsList.forEach {
        path.child(it.id).child(map[USER_ID].toString()).updateChildren(map)
    }

    path.child(CURRENT_UID).child(map[USER_ID].toString()).updateChildren(map)
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {
            function()
        }
}

fun sendMessageToGroup(message: String, groupId: String, messageTypeText: String, function: () -> Unit) {

    val refMessages = "$NODE_GROUPS/$groupId/$NODE_MESSAGES"
    val messageKey = REF_DATABASE_ROOT.child(refMessages).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[USER_ID] = messageKey.toString()
    mapMessage[MESSAGE_SENDER] = CURRENT_UID
    mapMessage[MESSAGE_TEXT] = message
    mapMessage[CHILD_TYPE] = messageTypeText
    mapMessage[MESSAGE_TIMESTAMP] = ServerValue.TIMESTAMP

    REF_DATABASE_ROOT.child(refMessages).child(messageKey.toString()).updateChildren(mapMessage)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}
