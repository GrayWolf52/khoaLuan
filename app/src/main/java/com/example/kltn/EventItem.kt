package com.example.kltn

import java.util.*

class EventItem(
    val id: Int,
    val date: Date,
    val type: Int,
    val name: String,
    val groupId: Int,
    val userIdSendEvent: Int,
    val userNameSendEvent: String,
  var status: Int
) {

}