package com.example.telegram.models

data class User(
    val id: String = "",
    var username: String = "",
    var fullname: String = "",
    var phone: String = "",
    var bio: String = "",
    var status: String = "",
    var avatarUrl: String = ""
)