package com.example.telegram.models

data class UserModel(
    val id: String = "",
    var username: String = "",
    var fullname: String = "",
    var phone: String = "",
    var bio: String = "",
    var state: String = "",
    var avatarUrl: String = "empty" // default link for Picasso
)