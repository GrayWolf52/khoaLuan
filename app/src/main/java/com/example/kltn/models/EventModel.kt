package com.example.kltn.models

import java.util.*

class EventModel(val id: Int) {
    var title: String = ""
    var description: String = ""
    var startDate: Date = Date()
    var endDate: Date = Date()
    var loopType: Int = 0
    var participants: List<UserModel> = ArrayList<UserModel>()
}