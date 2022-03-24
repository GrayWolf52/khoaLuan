package com.example.kltn.services

import com.example.kltn.models.EventModel
import com.example.kltn.models.GroupModel
import com.example.kltn.models.UserModel
import java.util.*

class EventService {
    companion object {
        fun get(userId: Int, month: Int, year: Int): List<EventModel> {
            var event = EventModel(1)
            event.title = "Sự kiện tháng " + month.toString() + "/" + year.toString()
            event.startDate = Date(year - 1900, month - 1, 1)
            event.endDate = Date(year - 1900, month - 1, 1)
            return listOf<EventModel>(event)
        }
    }
}