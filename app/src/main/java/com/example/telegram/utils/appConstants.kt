package com.example.telegram.utils

import com.example.telegram.MainActivity

lateinit var APP_ACTIVITY: MainActivity

const val MESSAGE_TYPE_IMAGE = "image"
const val MESSAGE_TYPE_TEXT = "text"
const val MESSAGE_TYPE_VOICE = "voice"
const val MESSAGE_TYPE_FILE = "file"

const val TYPE_PRIVATE_CHAT = "chat"
const val TYPE_GROUP_CHAT = "group"
const val TYPE_CHANNEL_CHAT = "channel"

const val PICK_FILE_REQUEST_CODE = 301