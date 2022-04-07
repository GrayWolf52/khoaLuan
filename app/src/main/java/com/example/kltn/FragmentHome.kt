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
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProviders
import java.util.*

class FragmentHome : Fragment() {
    private var recyclerView : RecyclerView? = null
    private var recyclerViewEvent : RecyclerView? = null
    private lateinit var dayViewModel: DayViewModel
    private var lbMonth: TextView? = null
    private lateinit var btnBack: TextView
    private lateinit var btnNext: TextView
    private lateinit var btnAddEvent: TextView
    lateinit var dayAdapter: DayAdapter
    lateinit var eventAdapter: EventAdapter
    private val calendar = Calendar.getInstance()
    private var userId = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View? {
        var bundle = arguments
        if (bundle != null)
            userId = bundle!!.getInt("UserId")
        var view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view?.findViewById(R.id.recyclerView)
        recyclerViewEvent = view?.findViewById(R.id.recyclerViewEvent)
        lbMonth = view?.findViewById(R.id.lbGroupScheduleMonth)
        btnNext = view.findViewById(R.id.btnGroupSchuduleNext)
        btnAddEvent = view.findViewById(R.id.btnAddGroup)
        btnAddEvent.setOnClickListener {
            val intent = Intent(this.requireActivity(), ActivityEditEvent::class.java)
            var b = Bundle()
            b.putInt("UserId", userId)
            b.putInt("EventId", 0)
            intent.putExtras(b)
            startActivity(intent)
        }
        dayAdapter = DayAdapter { view, day -> adapterDayOnClick(view, day)}
        eventAdapter = EventAdapter { view, event -> adapterEventOnClick(view, event)}
        recyclerView!!.adapter = dayAdapter
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
        btnBack = view.findViewById(R.id.btnGroupSchudulePrevious)
        btnBack.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            refreshEvent()
        }
        btnNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            refreshEvent()
        }
        refreshEvent()
        return view
    }
    fun adapterEventOnClick(view: View?, event: EventItem) {

    }
    fun adapterDayOnClick(view: View?, day: DayModel) {
        if (day.date == null) return;
    }
    fun refreshEvent() {
        var month = calendar.get(Calendar.MONTH) + 1
        var year = calendar.get(Calendar.YEAR)
        lbMonth!!.text = month.toString() + " / " + year.toString()
        Thread({
            dayViewModel.load(userId, 0, month, year)
        }).start()
    }
}