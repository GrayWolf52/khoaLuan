package com.example.kltn.models

import java.util.*

class EventModel(val id: Int) {
    var title: String = ""
    var description: String = ""
    var startTime: Date = Date()
    var endTime: Date = Date()
    var recurrenceType: Int = 0
    var groupId: Int = 0
    var statusEvent: Int = 0
    var place: String = ""
    var participants: List<UserModel> = ArrayList<UserModel>()
}

object StatusEvent {
    const val NEW = 0
    const val DOING = 1
    const val DONE = 2
}