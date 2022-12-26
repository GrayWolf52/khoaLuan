package com.example.kltn

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kltn.models.EventModel
import com.example.kltn.services.EventService
import java.util.*
import kotlin.collections.ArrayList

class DayViewModel() : ViewModel() {
    private val _listDay = MutableLiveData<List<DayModel?>>()
    val listDay: LiveData<List<DayModel?>>
        get() = _listDay
    private val _listEvent = MutableLiveData<List<EventItem?>>()
    val listEvent: LiveData<List<EventItem?>>
        get() = _listEvent
    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message
    private var autoId: Int = 0
    private var autoEventId: Int = 0
    private var listEventInfo: Array<EventInfos>? = arrayOf()

    var listEventLoopMonth = mutableListOf<EventModel>()
    var listEventLoopDay = mutableListOf<EventModel>()
    var listEventLoopWeek = mutableListOf<EventModel>()
    var listEventLoopSundayOnlyWeek = mutableListOf<EventModel>()

    fun clearDataListLoop() {
        listEventLoopSundayOnlyWeek.clear()
        listEventLoopWeek.clear()
        /*      listEventLoopMonth.clear()*/
        listEventLoopDay.clear()
    }

    fun clearDataMonth() {
        listEventLoopMonth.clear()
    }

    fun insertDay(day: DayModel?) {
        var updatedList = listDay.value?.toMutableList()
        try {
            if (updatedList == null) _listDay.postValue(listOf<DayModel?>(day))
            else {
                updatedList!!.add(day)
                _listDay.postValue(updatedList!!)
            }
        } catch (ex: Exception) {
        }
    }

    fun load(userId: Int, groupId: Int, month: Int, year: Int) {
        Log.d("TAG", "load: month = $month ")
        val listDayOfMonth = ArrayList<DayModel?>();
        var date = Date(year - 1900, month - 1, 1)
        var dayOfMonth = 1
        var cal = Calendar.getInstance()
        cal.setTime(date)
        var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == 1)
            dayOfWeek = 8
        for (i in 1 until (dayOfWeek - 1)) {
            listDayOfMonth.add(DayModel(++autoId, null))
        }
        do {
            var day = DayModel(++autoId, date);
            listDayOfMonth.add(day)
            dayOfMonth++
            date = Date(year - 1900, month - 1, dayOfMonth)
        } while (date!!.month == month - 1)
        cal.setTime(Date(year - 1900, month - 1, dayOfMonth - 1))
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == 1) dayOfWeek = 8
        for (i in dayOfWeek - 1 until 7) {
            listDayOfMonth.add(DayModel(++autoId, null))
        }
        Log.d("TAG", "load: size listEventLoopMonth ${listEventLoopMonth.size} ")
        for (day in listDayOfMonth) {
            if (day == null || day!!.date == null) continue
            var cal1 = Calendar.getInstance()
            cal1.setTime(day.date)

            listEventLoopDay.forEach {
                val cal3 = Calendar.getInstance()
                cal3.setTime(it.startTime)
                if (cal1.get(Calendar.DAY_OF_MONTH) >= cal3.get(Calendar.DAY_OF_MONTH)
                    && cal1.get(Calendar.MONTH) == cal3.get(Calendar.MONTH)
                    && cal1.get(Calendar.YEAR) == cal3.get(Calendar.YEAR)
                )
                    day.status2 = true
            }
            listEventLoopMonth.forEach {
                val cal3 = Calendar.getInstance()
                cal3.setTime(it.startTime)
                Log.d("TAG", "load: start date = ${it.startTime}")
                if (cal1.get(Calendar.DAY_OF_MONTH) == cal3.get(Calendar.DAY_OF_MONTH)
                    && cal1.get(Calendar.MONTH) >= (cal3.get(Calendar.MONTH) + 1)
                    && cal1.get(Calendar.YEAR) == cal3.get(Calendar.YEAR)
                )
                    day.status2 = true
            }


        }

        var events = ArrayList<EventItem?>()
        var resultEvent = EventService.get(userId, groupId, month, year)
        listEventInfo = resultEvent.second
        if (resultEvent.first.length > 0) {
            _message.postValue(resultEvent.first)
        } else {
            var listEvent = resultEvent.second
            if (listEvent != null)
                for (event in listEvent) {
                    events.add(
                        EventItem(
                            event.id,
                            event.startTime,
                            1,
                            event.title,
                            event.groupId,
                            event.statusEvent,
                            event.creator.id,
                            event.creator.userName,
                            event.status
                        )
                    )

                    EventService.getById(event.id).second?.let {
                        Log.d("TAG", "load: event model = ${it.recurrenceType}")
                        when (it.recurrenceType) {
                            1 -> listEventLoopDay.add(it)
                            2 -> listEventLoopWeek.add(it)
                            4 -> listEventLoopSundayOnlyWeek.add(it)
                            3 -> listEventLoopMonth.add(it)
                            else -> Log.d("TAG", "load: not found loop")
                        }
                    }

                    for (day in listDayOfMonth) {
                        if (day == null || day!!.date == null) continue
                        var cal1 = Calendar.getInstance()
                        var cal2 = Calendar.getInstance()
                        cal1.setTime(day.date)
                        cal2.setTime(event.startTime)
                        if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
                            && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                            && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                        )
                            day.status2 = true

                        listEventLoopDay.forEach {
                            val cal3 = Calendar.getInstance()
                            cal3.setTime(it.startTime)
                            if (cal1.get(Calendar.DAY_OF_MONTH) >= cal3.get(Calendar.DAY_OF_MONTH)
                                && cal1.get(Calendar.MONTH) == cal3.get(Calendar.MONTH)
                                && cal1.get(Calendar.YEAR) == cal3.get(Calendar.YEAR)
                            )
                                day.status2 = true
                        }

                        listEventLoopWeek.forEach {
                            val cal3 = Calendar.getInstance()
                            cal3.setTime(it.startTime)
                            if (cal1.get(Calendar.DAY_OF_WEEK) == cal3.get(Calendar.DAY_OF_WEEK)
                                && cal1.get(Calendar.MONTH) == cal3.get(Calendar.MONTH)
                                && cal1.get(Calendar.YEAR) == cal3.get(Calendar.YEAR) && cal1.get(
                                    Calendar.DAY_OF_MONTH
                                ) >= cal3.get(Calendar.DAY_OF_MONTH)
                            )
                                day.status2 = true
                        }
                    }
                }
        }
        _listDay.postValue(listDayOfMonth!!)
        _listEvent.postValue(events!!)

    }


    fun updateStatusEvent(statusEvent: Int, idEvent: Int) {
        listEventInfo?.toList()?.find { eventInfos -> eventInfos.id == idEvent }?.let {
            EventService.update(
                it.id,
                it.title,
                it.description,
                it.startTime,
                it.endTime,
                it.recurrenceType,
                it.groupId,
                it.participants.map { userInfos -> userInfos.id },
                it.creator.id,
                it.place,
                statusEvent
            )
        }
    }

    fun updateStatus1(id: Int, value: Boolean) {
        var updatedList = listDay.value?.toMutableList() ?: return
        try {
            var n = updatedList.count()
            for (i in 0 until n) {
                if (updatedList[i]!!.id == id) {
                    var day = updatedList[i]!!
                    updatedList.removeAt(i);
                    var newDay = DayModel(day.id, day.date)
                    newDay.status1 = value
                    newDay.status2 = day.status2
                    updatedList.add(i, newDay)
                    break
                }
            }
            _listDay.postValue(updatedList)
        } catch (ex: Exception) {
            var a = 1
        }
    }

    /*   fun insertEvent(date: Date, type: Int, name: String, groupId: Int) {
           var updatedList = listEvent.value?.toMutableList()
           try {
               if (updatedList == null) _listEvent.postValue(
                   listOf<EventItem?>(
                       EventItem(
                           ++autoEventId,
                           date,
                           type,
                           name, groupId
                       )
                   )
               )
               else {
                   var n = updatedList.count()
                   for (i in 0 until n + 1) {
                       if (i < n && updatedList[i]!!.date <= date) continue
                       updatedList!!.add(
                           i,
                           EventItem(++autoEventId, date, type, name, groupId = groupId)
                       )
                       break
                   }
                   _listEvent.postValue(updatedList!!)
               }
               var currentListDay = listDay.value?.toMutableList()
               if (currentListDay != null) {
                   var n = currentListDay.count()
                   for (i in 0 until n) {
                       var day = currentListDay[i]!!
                       if (day!!.date != null && day!!.date == date) {
                           currentListDay.removeAt(i);
                           var newDay = DayModel(day.id, day.date)
                           newDay.status1 = true
                           newDay.status2 = true
                           currentListDay.add(i, newDay)
                           break
                       }
                   }
                   _listDay.postValue(currentListDay)
               }
           } catch (ex: Exception) {
           }
       }*/

    /* fun updateStatus2(id: Int, value: Boolean) {
         var updatedList = listDay.value?.toMutableList() ?: return
         try {
             var n = updatedList.count()
             for (i in 0 until n) {
                 if (updatedList[i]!!.id == id) {
                     var day = updatedList[i]!!
                     updatedList.removeAt(i);
                     var newDay = DayModel(day.id, day.date)
                     newDay.status1 = day.status1
                     newDay.status2 = value
                     updatedList.add(i, newDay)
                     break
                 }
             }
             _listDay.postValue(updatedList)
         } catch (ex: Exception) {
         }
     }*/
}

class DayViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DayViewModel::class.java)) {
            var model: DayViewModel = DayViewModel()
            return model as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}