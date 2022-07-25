package com.example.kltn.models

data class DataSuggest(
    val shortname: String = "",
    val fullname: String = "",
    val type: Int = 0,
    val users: List<UserModel>
)