package com.example.kltn

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProviders
import java.time.LocalDate
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var recyclerView : RecyclerView
    lateinit var dayViewModel: DayViewModel
    lateinit var lbMonth: TextView
    lateinit var dayAdapter: DayAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView);
        lbMonth = findViewById(R.id.lbMonth)
        dayAdapter = DayAdapter { view, day -> adapterDayOnClick(view, day)}
        recyclerView.adapter = dayAdapter
        dayViewModel = ViewModelProviders.of(this, DayViewModelFactory(this.applicationContext as Context)).get(DayViewModel::class.java)
        var countMonth = 1;
        dayViewModel.listDay.observe(this, Observer {
            it?.let {
                dayAdapter.submitList(it as MutableList<DayModel>)
            }
        })
        lbMonth.setOnClickListener({
            val now = Date()
            countMonth++
            dayViewModel.insertMonth(now.month + countMonth, now.year + 1900)
        })
        val now = Date()
        dayViewModel.insertMonth(now.month + countMonth, now.year + 1900)
    }

    fun adapterDayOnClick(view: View?, day: DayModel) {
        if (!day.status1) {
            dayViewModel.updateStatus1(day.id, true)
            return
        }

        if (!day.status2) {
            dayViewModel.updateStatus2(day.id, true)
        }

    }
}