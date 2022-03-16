package com.example.kltn

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*
import kotlin.collections.ArrayList

class DayViewModel : ViewModel() {
    private val _listDay = MutableLiveData<List<DayModel?>>()
    val listDay: LiveData<List<DayModel?>>
        get() = _listDay
    private val _listEvent = MutableLiveData<List<EventItem?>>()
    val listEvent: LiveData<List<EventItem?>>
        get() = _listEvent

    private var autoId: Int = 0
    private var autoEventId: Int = 0

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

    fun setMonth(month: Int, year: Int) {
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
        _listDay.postValue(listDayOfMonth!!)
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
    fun insertEvent(date: Date, type: Int, name: String) {
        var updatedList = listEvent.value?.toMutableList()
        try {
            if (updatedList == null) _listEvent.postValue(listOf<EventItem?>(EventItem(++autoEventId, date, type, name)))
            else {
                var n = updatedList.count()
                for (i in 0 until n + 1) {
                    if (i < n && updatedList[i]!!.date <= date) continue
                    updatedList!!.add(i, EventItem(++autoEventId, date, type, name))
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
    }
    fun updateStatus2(id: Int, value: Boolean) {
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
    }
}

class DayViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun<T: ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DayViewModel::class.java)) {
            var model: DayViewModel = DayViewModel()
            return model as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}