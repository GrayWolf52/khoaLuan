package com.example.kltn

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.services.EventService
import java.util.*

class ActivityStaffSchedule : AppCompatActivity() {
    private lateinit var btnStaffScheduleBack: TextView
    private lateinit var lbExtendInfor: TextView
    private lateinit var llExtendedInfor: LinearLayout
    private lateinit var rcvStaffSchedule: RecyclerView
    private lateinit var lbStaffScheduleMonth: TextView
    private lateinit var btnStaffSchedulePrevious: TextView
    private lateinit var btnStaffScheduleNext: TextView
    private var extendedInfor = false
    private lateinit var eventAdapter: EventAdapter
    private var autoId = 0
    private val calendar = Calendar.getInstance()
    private val userId = 1
    private val groupId = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staffschedule)
        btnStaffScheduleBack = findViewById(R.id.btnStaffScheduleBack)
        btnStaffScheduleBack.setOnClickListener {
            finish()
        }
        llExtendedInfor = findViewById(R.id.llExtendedInfor)
        lbExtendInfor = findViewById(R.id.lbExtendInfor)
        lbStaffScheduleMonth = findViewById(R.id.lbGroupScheduleMonth)
        btnStaffSchedulePrevious = findViewById(R.id.btnGroupSchudulePrevious)
        btnStaffSchedulePrevious.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            refreshEvent()
        }
        btnStaffScheduleNext = findViewById(R.id.btnGroupSchuduleNext)
        btnStaffScheduleNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            refreshEvent()
        }
        lbExtendInfor.setOnClickListener {
            extendedInfor = !extendedInfor
            if (extendedInfor) {
                lbExtendInfor.text = "Ẩn bớt"
                llExtendedInfor.visibility = View.VISIBLE
            }
            else {
                lbExtendInfor.text = "Xem thêm"
                llExtendedInfor.visibility = View.GONE
            }
        }
        rcvStaffSchedule = findViewById(R.id.rcvGroupSchedule)
        eventAdapter = EventAdapter { view, event -> adapterEventOnClick(view, event)}
        rcvStaffSchedule.adapter = eventAdapter
        refreshEvent()
    }

    fun adapterEventOnClick(view: View?, event: EventItem) {

    }

    fun refreshEvent() {
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        lbStaffScheduleMonth.text = month.toString() + " / " + year.toString()
        val listDayOfMonth = ArrayList<DayModel?>();
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == 1) dayOfWeek = 8
        for (i in dayOfWeek - 1 until 7) {
            listDayOfMonth.add(DayModel(++autoId, null))
        }
        var events = ArrayList<EventItem?>()
        var resultEvent = EventService.get(userId, groupId, month, year)
        if (resultEvent.first.length > 0) {
            Toast.makeText(this, resultEvent.first, Toast.LENGTH_SHORT).show()
        }
        else {
            var listEvent = resultEvent.second
            if (listEvent != null)
                for (event in listEvent!!) {
                    events.add(EventItem(1, event.startTime, 1, event.title))
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
                    }
                }
        }
        eventAdapter.submitList(events.toMutableList())
    }
}