package com.example.telegram.utils

import android.net.Uri
import android.provider.ContactsContract
import com.example.telegram.models.CommonModel
import com.example.telegram.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference

lateinit var CURRENT_UID: String
lateinit var USER: UserModel

const val FOLDER_USER_AVATAR = "user_avatar"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_CONTACTS = "contacts"

const val USER_ID = "id"
const val USER_PHONE = "phone"
const val USER_USERNAME = "username"
const val USER_FULLNAME = "fullname"
const val USER_BIO = "bio"
const val USER_AVATAR_URL = "avatarUrl"
const val USER_STATE = "state"

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

fun initContacts() {
    if (checkPermission(READ_CONTACTS)) {
        val arrayContacts = arrayListOf<CommonModel>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        // When cursor != null
        cursor?.let {
            while (it.moveToNext()) {
                val fullName =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel()
                newModel.fullname = fullName
                newModel.phone = phone.replace(Regex("[\\s,-]"), "")
                arrayContacts.add(newModel)
            }
        }

        cursor?.close()

        addContactsToDatabase(arrayContacts)
    }
}

fun addContactsToDatabase(arrayContacts: ArrayList<CommonModel>) {
    REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener {
        it.children.forEach { snapshot ->
            arrayContacts.forEach { contact ->
                if (snapshot.key == contact.phone) {
                    REF_DATABASE_ROOT.child(NODE_CONTACTS).child(CURRENT_UID)
                        .child(snapshot.value.toString()).child(USER_ID)
                        .setValue(snapshot.value.toString())
                        .addOnFailureListener { showToast(it.message.toString()) }
                }
            }
        }
    })
}

// Function transforms data from Firebase to CommonModel
fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

// Function transforms data from Firebase to UserModel
fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()
