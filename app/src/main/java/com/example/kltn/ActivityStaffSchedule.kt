package com.example.kltn

import android.os.Bundle
import android.os.PersistableBundle
import android.os.RecoverySystem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ActivityStaffSchedule : AppCompatActivity() {
    private lateinit var btnStaffScheduleBack: TextView
    private lateinit var lbExtendInfor: TextView
    private lateinit var llExtendedInfor: LinearLayout
    private lateinit var rcvStaffSchedule: RecyclerView
    private var extendedInfor = false
    private lateinit var eventAdapter: EventAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staffschedule)
        btnStaffScheduleBack = findViewById(R.id.btnStaffScheduleBack)
        btnStaffScheduleBack.setOnClickListener {
            finish()
        }
        llExtendedInfor = findViewById(R.id.llExtendedInfor)
        lbExtendInfor = findViewById(R.id.lbExtendInfor)
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
        rcvStaffSchedule = findViewById(R.id.rcvStaffSchedule)
        eventAdapter = EventAdapter { view, event -> adapterEventOnClick(view, event)}
        rcvStaffSchedule.adapter = eventAdapter
        eventAdapter.submitList(listOf<EventItem?>(EventItem(1, Date(), 1, "Sự kiện")))
    }

    fun adapterEventOnClick(view: View?, event: EventItem) {

    }
}