package com.example.kltn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProviders
import com.example.kltn.models.EventModel
import com.example.kltn.services.EventService
import java.util.*

class FragmentHome : Fragment() {
    private var recyclerView : RecyclerView? = null
    private var recyclerViewEvent : RecyclerView? = null
    lateinit var dayViewModel: DayViewModel
    private var lbMonth: TextView? = null
    private lateinit var btnBack: TextView
    private lateinit var btnNext: TextView
    private lateinit var btnAddEvent: TextView
    lateinit var dayAdapter: DayAdapter
    lateinit var eventAdapter: EventAdapter
    var countMonth = 0
    val now = Date()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View? {
        var view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view?.findViewById(R.id.recyclerView)
        recyclerViewEvent = view?.findViewById(R.id.recyclerViewEvent)
        lbMonth = view?.findViewById(R.id.lbMonth)
        btnNext = view.findViewById(R.id.btnNext)
        btnAddEvent = view.findViewById(R.id.btnAddGroup)
        btnAddEvent.setOnClickListener {
            val intent = Intent(this.requireActivity(), ActivityEditEvent::class.java)
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
        dayViewModel.userId = 1
        btnBack = view.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            countMonth--
            lbMonth!!.text = ((now.month + countMonth) % 12 + 1).toString() + "/" + (now.year + ((now.month + countMonth) / 12) + 1900)
            dayViewModel.setMonth((now.month + countMonth) % 12 + 1, now.year + ((now.month + countMonth) / 12) + 1900)
        }
        btnNext.setOnClickListener {
            countMonth++
            lbMonth!!.text = ((now.month + countMonth) % 12 + 1).toString() + "/" + (now.year + ((now.month + countMonth) / 12) + 1900)
            dayViewModel.setMonth((now.month + countMonth) % 12 + 1, now.year + ((now.month + countMonth) / 12) + 1900)
        }
        lbMonth!!.text = (now.month + 1).toString() + "/" + (now.year + 1900)
        dayViewModel.setMonth(now.month + countMonth, now.year + 1900)
        return view
    }
    fun adapterEventOnClick(view: View?, event: EventItem) {

    }
    fun adapterDayOnClick(view: View?, day: DayModel) {
        if (day.date == null) return;
    }
}