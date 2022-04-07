package com.example.kltn

import java.util.*

class EventInfos {
    var id: Int = 0
    var title: String = ""
    var description: String = ""
    var startTime: Date = Date()
    var endTime: Date = Date()
    var recurrenceType: Int = 0
    var groupId: Int = 0
    var creatorId: Int = 0
    var participants: ArrayList<UserInfos> = ArrayList<UserInfos>()
}