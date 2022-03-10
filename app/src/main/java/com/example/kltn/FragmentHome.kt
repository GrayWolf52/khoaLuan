package com.example.kltn

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
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
    lateinit var dayViewModel: DayViewModel
    private var lbMonth: TextView? = null
    lateinit var dayAdapter: DayAdapter
    lateinit var eventAdapter: EventAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View? {
        var view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view?.findViewById(R.id.recyclerView)
        recyclerViewEvent = view?.findViewById(R.id.recyclerViewEvent)
        lbMonth = view?.findViewById(R.id.lbMonth)
        dayAdapter = DayAdapter { view, day -> adapterDayOnClick(view, day)}
        eventAdapter = EventAdapter { view, event -> adapterEventOnClick(view, event)}
        recyclerView!!.adapter = dayAdapter
        recyclerViewEvent!!.adapter = eventAdapter
        dayViewModel = ViewModelProviders.of(this, DayViewModelFactory(context as Context)).get(DayViewModel::class.java)
        var countMonth = 1;
        dayViewModel.listDay.observe(this.requireActivity(), Observer {
            it?.let {
                dayAdapter.submitList(it as MutableList<DayModel>)
            }
        })
        dayViewModel.listEvent.observe(this.requireActivity(), Observer {
            it?.let {
                eventAdapter.submitList(it as MutableList<EventModel>)
            }
        })
        lbMonth!!.setOnClickListener({
            val now = Date()
            countMonth++
            dayViewModel.insertMonth(now.month + countMonth, now.year + 1900)
        })
        val now = Date()
        dayViewModel.insertMonth(now.month + countMonth, now.year + 1900)
        return view
    }
    fun adapterEventOnClick(view: View?, event: EventModel) {

    }
    fun adapterDayOnClick(view: View?, day: DayModel) {
        if (day.date != null && !day.status1) {
            dayViewModel.insertEvent(day.date, 1, "Sự kiện")
            return
        }

    }
}