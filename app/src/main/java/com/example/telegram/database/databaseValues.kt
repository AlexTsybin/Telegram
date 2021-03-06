package com.example.telegram.database

import com.example.telegram.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var CURRENT_UID: String
lateinit var USER: UserModel

const val FOLDER_USER_AVATAR = "user_avatar"
const val FOLDER_FILES = "message_files"
const val FOLDER_GROUPS_AVATARS = "groups_avatars"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_CONTACTS = "contacts"
const val NODE_MESSAGES = "messages"
const val NODE_CHATS_LIST = "chats_list"
const val NODE_GROUPS = "groups"
const val NODE_MEMBERS = "members"

const val USER_ID = "id"
const val USER_PHONE = "phone"
const val USER_USERNAME = "username"
const val USER_FULLNAME = "fullname"
const val USER_BIO = "bio"
const val USER_AVATAR_URL = "avatarUrl"
const val USER_STATE = "state"

// Chat fields
const val MESSAGE_TEXT = "messageText"
const val CHILD_TYPE = "type"
const val MESSAGE_SENDER = "messageSender"
const val MESSAGE_TIMESTAMP = "timeStamp"
const val MESSAGE_FILE_URL = "fileUrl"

// Group fields
const val USER_CREATOR = "creator"
const val USER_ADMIN = "admin"
const val USER_MEMBER = "member"