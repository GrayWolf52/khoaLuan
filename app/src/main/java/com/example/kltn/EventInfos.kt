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
    var creator: UserInfos = UserInfos()
    var participants: ArrayList<UserInfos> = ArrayList<UserInfos>()
    var status: Int = 0
    var place: String = ""
    var statusEvent: Int = 0
}

object Status {
    const val NOT_YET_ACCEPT = 1
    const val ACCEPTED = 2
    const val DENY_ACCEPT = 3
}