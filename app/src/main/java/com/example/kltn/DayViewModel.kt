package com.example.kltn

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*

class DayViewModel : ViewModel() {
    private val _listDay = MutableLiveData<List<DayModel?>>()
    val listDay: LiveData<List<DayModel?>>
        get() = _listDay

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

    fun insertMonth(month: Int, year: Int) {
        val listDayOfMonth = ArrayList<DayModel?>();
        var date = Date(year - 1900, month - 1, 1)
        var dayOfMonth = 1
        var cal = Calendar.getInstance()
        cal.setTime(date)
        var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        for (i in 1 until (dayOfWeek - 1)) {
            listDayOfMonth.add(DayModel(null))
        }
        do {
            listDayOfMonth.add(DayModel(date))
            dayOfMonth++
            date = Date(year - 1900, month - 1, dayOfMonth)
        } while (date!!.month == month - 1)
        cal.setTime(Date(year - 1900, month - 1, dayOfMonth - 1))
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        for (i in dayOfWeek - 1 until 7) {
            listDayOfMonth.add(DayModel(null))
        }
        var updatedList = _listDay.value?.toMutableList()
        try {
            if (updatedList == null) _listDay.postValue(listDayOfMonth)
            else {
                updatedList!!.addAll(listDayOfMonth)
                _listDay.postValue(updatedList!!)
            }
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