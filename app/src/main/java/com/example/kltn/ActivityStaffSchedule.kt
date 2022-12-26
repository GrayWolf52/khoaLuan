package com.example.kltn

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.services.EventService
import com.example.kltn.services.UserService
import com.example.kltn.utils.Constants
import java.util.*
import androidx.fragment.app.FragmentActivity

class ActivityStaffSchedule : AppCompatActivity() {
    private lateinit var btnStaffScheduleBack: TextView
//    private lateinit var rcvStaffSchedule: RecyclerView
//    private lateinit var lbStaffScheduleMonth: TextView
//    private lateinit var btnStaffSchedulePrevious: TextView
//    private lateinit var btnStaffScheduleNext: TextView
//    private lateinit var eventAdapter: EventAdapter
//    private val calendar = Calendar.getInstance()
//    private var userId = 0
//    private val groupId = 1

    private lateinit var txtUserNameProfile: TextView
    private lateinit var txtNameProfile: TextView
    private lateinit var txtPhoneProfile: TextView
    private lateinit var txtAddressProfile: TextView

    private lateinit var recyclerViewEvent: RecyclerView
    private var lbMonth: TextView? = null
    private lateinit var btnBack: TextView
    private lateinit var btnNext: TextView
    lateinit var eventAdapter: EventAdapter
    private val calendar = Calendar.getInstance()
    private var userId = -1
    private var groupId = 0
    private var listEventItem = mutableListOf<EventItem?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        var bundle = intent.extras
        userId = bundle!!.getInt(Constants.USER_ID)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staffschedule)
        btnStaffScheduleBack = findViewById(R.id.btnStaffScheduleBack)
        btnStaffScheduleBack.setOnClickListener {
            finish()
        }

        lbMonth = findViewById(R.id.lbGroupScheduleMonth)
        btnBack = findViewById(R.id.btnGroupSchudulePrevious)
        btnBack.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            refreshEvent()
        }
        btnNext = findViewById(R.id.btnGroupSchuduleNext)
        btnNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            refreshEvent()
        }
//
        //recyclerViewEvent = findViewById(R.id.rcvGroupSchedule)
        eventAdapter =
            EventAdapter(this, { view, event -> adapterEventOnClick(view, event) }, { view, event ->
            }, { event, isAccept, position ->
            }, { event, status -> })
        recyclerViewEvent.adapter = eventAdapter
//        rcvStaffSchedule.adapter = eventAdapter
        refreshEvent()
        txtUserNameProfile = findViewById(R.id.txtUsernameProfile)
        txtNameProfile = findViewById(R.id.txtNameProfile)
        txtPhoneProfile = findViewById(R.id.txtPhoneProfile)
        txtAddressProfile = findViewById(R.id.txtAddressProfile)
        loadProfile()
    }

    private fun adapterEventOnClick(view: View?, event: EventItem) {
        var intent = Intent(this, ActivityEditEvent::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        intent.putExtra(Constants.EVENT_ID, event.id)
        intent.putExtra(Constants.GROUD_ID, event.groupId)
        startActivity(intent)
    }

    fun refreshEvent() {
        var month = calendar.get(Calendar.MONTH) + 1
        var year = calendar.get(Calendar.YEAR)
        Thread {
            var result = EventService.get(userId, groupId, month, year)
            runOnUiThread {
                if (result.first.isNotEmpty())
                    Toast.makeText(this, result.first, Toast.LENGTH_SHORT).show()
                else
                    eventAdapter.submitList(listEventItem)
            }
        }.start()
    }

    //
//    fun refreshEvent() {
//        val month = calendar.get(Calendar.MONTH) + 1
//        val year = calendar.get(Calendar.YEAR)
//        lbStaffScheduleMonth.text = month.toString() + " / " + year.toString()
//        val listDayOfMonth = ArrayList<DayModel?>();
//        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
//        if (dayOfWeek == 1) dayOfWeek = 8
//        for (i in dayOfWeek - 1 until 7) {
//            listDayOfMonth.add(DayModel(++autoId, null))
//        }
//        var events = ArrayList<EventItem?>()
//        var resultEvent = EventService.get(userId, groupId, month, year)
//        if (resultEvent.first.length > 0) {
//            Toast.makeText(this, resultEvent.first, Toast.LENGTH_SHORT).show()
//        } else {
//            var listEvent = resultEvent.second
//            if (listEvent != null)
//                for (event in listEvent!!) {
//                    events.add(
//                        EventItem(
//                            event.id,
//                            event.startTime,
//                            1,
//                            event.title,
//                            event.groupId,
//                            event.creator.id,
//                            event.creator.userName,
//                            event.status
//                        )
//                    )
//                    for (day in listDayOfMonth) {
//                        if (day == null || day!!.date == null) continue
//                        var cal1 = Calendar.getInstance()
//                        var cal2 = Calendar.getInstance()
//                        cal1.setTime(day.date)
//                        cal2.setTime(event.startTime)
//                        if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
//                            && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
//                            && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
//                        )
//                            day.status2 = true
//                    }
//                }
//        }
//        eventAdapter.submitList(events.toMutableList())
//    }
    private fun loadProfile() {
        Thread {
            var result = UserService.getUser(userId)
            if (result.first.isEmpty()) {
                if (result.second != null) {
                    runOnUiThread {
                        var descrip = result.second
                        descrip?.username?.let {
                            txtUserNameProfile.setText(it)
                        }
                        txtNameProfile.setText(descrip?.firstName + " " + descrip?.lastName)

                        descrip?.phone?.let {
                            txtPhoneProfile.setText(it)
                        }
                        descrip?.address?.let {
                            txtAddressProfile.setText(it)
                        }
                    }
                }
            } else {
                Toast.makeText(this, result.first, Toast.LENGTH_SHORT).show()
            }

        }.start()

    }
}