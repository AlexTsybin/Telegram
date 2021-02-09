package com.example.telegram.models

data class CommonModel(
    val id: String = "",
    var username: String = "",
    var fullname: String = "",
    var phone: String = "",
    var bio: String = "",
    var state: String = "",
    var avatarUrl: String = "empty", // default link for Picasso

    // Chat fields
    var messageText: String = "",
    var messageType: String = "",
    var messageSender: String = "",
    var timeStamp: Any = "",
    var imageUrl: String = "empty", // default link for Picasso
) {
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}