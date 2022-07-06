package com.example.kltn.models

data class UserModel(
    val id: Int,
    val username: String,
    var firstname: String = "",
    var lastname: String = "",
    var phone: String = "",
    var address: String = "",
    var roles : Int = 2
)