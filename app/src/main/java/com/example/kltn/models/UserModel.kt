package com.example.kltn.models

data class UserModel(
    val id: Int,
    val username: String,
    val fullname: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var phone: String = "",
    var address: String = "",
    var roles : Int = 2
)