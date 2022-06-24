package com.example.kltn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class FragmentSchedule : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var recyclerViewEvent: RecyclerView? = null
    private lateinit var lbGroupScheduleMonth: TextView
    private lateinit var btnGroupSchudulePrevious: TextView
    private lateinit var btnGroupSchuduleNext: TextView
    lateinit var dayViewModel: DayViewModel
    lateinit var dayAdapter: DayAdapter
    lateinit var eventAdapter: EventAdapter
    private var userId = -1
    private var groupId = 0
    private val calendar = Calendar.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var bundle = arguments
        if (bundle != null) {
            userId = bundle!!.getInt("UserId")
            groupId = bundle!!.getInt("GroupId")
        }

        var view = inflater.inflate(R.layout.fragment_schedule, container, false)
        lbGroupScheduleMonth = view.findViewById(R.id.lbGroupScheduleMonth)

        btnGroupSchudulePrevious = view.findViewById(R.id.btnGroupSchudulePrevious)
        btnGroupSchudulePrevious.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            refreshEvent()
        }

        btnGroupSchuduleNext = view.findViewById(R.id.btnGroupSchuduleNext)
        btnGroupSchuduleNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            refreshEvent()
        }
        recyclerView = view.findViewById(R.id.recyclerView)
        dayAdapter = DayAdapter { view, day -> adapterDayOnClick(view, day) }
        recyclerView!!.adapter = dayAdapter

        recyclerViewEvent = view.findViewById(R.id.recyclerViewEvent)
        eventAdapter = EventAdapter { view, event -> adapterEventOnClick(view, event) }
        recyclerViewEvent!!.adapter = eventAdapter

        dayViewModel = ViewModelProviders.of(this, DayViewModelFactory(context as Context)).get(DayViewModel::class.java)
        dayViewModel.listDay.observe(this.requireActivity(), Observer {
            it?.let {
                dayAdapter.submitList(it as MutableList<DayModel>)
            }
        })
        dayViewModel.listEvent.observe(this.requireActivity(), Observer {
            it?.let {
                eventAdapter.submitList(it as MutableList<EventItem>)
            }
        })
        dayViewModel.message.observe(this.requireActivity(), Observer {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        refreshEvent()

        return view
    }

    fun adapterDayOnClick(view: View?, day: DayModel) {
        if (day.date == null) return
    }

    fun adapterEventOnClick(view: View?, event: EventItem) {
        var intent = Intent(context, ActivityEditEvent::class.java)
        intent.putExtra("UserId", userId)
        intent.putExtra("EventId", event.id)
        startActivity(intent)
    }
    fun refreshEvent() {
        var month = calendar.get(Calendar.MONTH) + 1
        var year = calendar.get(Calendar.YEAR)
        lbGroupScheduleMonth!!.text = month.toString() + " / " + year.toString()
        Thread({
            dayViewModel.load(0, groupId, month, year)
        }).start()
    }
}