package com.example.kltn

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.kltn.extensions.toLiveData
import com.example.kltn.models.StatusEvent
import com.example.kltn.services.EventService
import com.example.kltn.services.UserGroupService
import com.example.kltn.services.UserService
import kotlinx.coroutines.launch

class HomeViewModel() : ViewModel() {

    private val _inforChart = MutableLiveData(Triple(0, 0, 0))
    val inforChart: LiveData<Triple<Int, Int, Int>> = _inforChart.toLiveData()

    fun getInforChart(userId: Int, month: Int, year: Int) {
        viewModelScope.launch {

            var groupId = UserGroupService.GetForUser(userId).second?.let {
                it.find {
                    it.userId == userId
                }
            }?.group?.id?.let {
                it
            } ?: 0
            Thread{
                val listStatusEvent = EventService.getListStatusByMonth(userId, groupId, month, year)
                var countStatusNew = 0
                var countStatusDoing = 0
                var countStatusDone = 0
                listStatusEvent.forEach {
                    when (it) {
                        StatusEvent.NEW -> countStatusNew++
                        StatusEvent.DOING -> countStatusDoing++
                        StatusEvent.DONE -> countStatusDone++
                    }
                }
                _inforChart.postValue(Triple(countStatusNew, countStatusDoing, countStatusDone))
            }.start()
        }
    }
}


class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            var model: HomeViewModel = HomeViewModel()
            return model as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}