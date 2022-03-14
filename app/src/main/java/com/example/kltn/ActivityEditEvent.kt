package com.example.kltn

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.time.hours

class ActivityEditEvent : AppCompatActivity() {
    private lateinit var btnEditEventBack: TextView
    private lateinit var txtEditEventStartTime: TextView
    private lateinit var txtEditEventStartDate: TextView
    private lateinit var txtEditEventEndTime: TextView
    private lateinit var txtEditEventEndDate: TextView
    private lateinit var txtEditEventParticipant: AutoCompleteTextView
    private lateinit var recyclerViewParticipant: RecyclerView
    private var listParticipant = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editevent)
        btnEditEventBack = findViewById(R.id.btnEditEventBack)
        txtEditEventStartTime = findViewById(R.id.txtEditEventStartTime)
        txtEditEventStartDate = findViewById(R.id.txtEditEventStartDate)
        txtEditEventEndTime = findViewById(R.id.txtEditEventEndTime)
        txtEditEventEndDate = findViewById(R.id.txtEditEventEndDate)
        txtEditEventParticipant = findViewById(R.id.txtEditEventParticipant)
        recyclerViewParticipant = findViewById(R.id.recyclerViewParticipant)
        btnEditEventBack.setOnClickListener {
            finish()
        }
        val calendar = Calendar.getInstance()

        val startDatePicker = object: DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                txtEditEventStartDate.text = dateToString(dayOfMonth, month, year)
            }
        }
        val endDatePicker = object: DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                txtEditEventEndDate.text = dateToString(dayOfMonth, month, year)
            }
        }
        txtEditEventStartDate.setOnClickListener {
            val startDatePickerDialog = DatePickerDialog(this, startDatePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            startDatePickerDialog.show()
        }
        txtEditEventEndDate.setOnClickListener {
            val endDatePickerDialog = DatePickerDialog(this, endDatePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            endDatePickerDialog.show()
        }

        val startTimePicker = object: TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, month: Int, dayOfMonth: Int) {
                txtEditEventStartTime.text = timeToString(dayOfMonth, month)
            }
        }
        val endTimePicker = object: TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, month: Int, dayOfMonth: Int) {
                txtEditEventEndTime.text = timeToString(dayOfMonth, month)
            }
        }
        txtEditEventStartTime.setOnClickListener {
            val startTimePickerDialog = TimePickerDialog(this, startTimePicker, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            startTimePickerDialog.show()
        }
        txtEditEventEndTime.setOnClickListener {
            val endTimePickerDialog = TimePickerDialog(this, endTimePicker, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            endTimePickerDialog.show()
        }

        txtEditEventStartTime.text = timeToString(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        txtEditEventStartDate.text = dateToString(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR))

        var calendarNext = Calendar.getInstance()
        calendarNext.add(Calendar.HOUR, 1)
        txtEditEventEndTime.text = timeToString(calendarNext.get(Calendar.HOUR_OF_DAY), calendarNext.get(Calendar.MINUTE))
        txtEditEventEndDate.text = dateToString(calendarNext.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR))

        var selectedParticipantAdapter = AdapterParticipant { view, participant -> deleteParticipant(view, participant) }
        recyclerViewParticipant.adapter = selectedParticipantAdapter
        val participants = arrayOf("Người tham dự 1", "Người tham dự 2", "0123456789", "0987654321")
        val participantAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, participants)
        txtEditEventParticipant.threshold = 2
        txtEditEventParticipant.setAdapter(participantAdapter)
        txtEditEventParticipant.setOnItemClickListener {parent, view, position, id ->
            txtEditEventParticipant.setText("")
            txtEditEventParticipant.clearFocus()
            listParticipant.add("")
            selectedParticipantAdapter.submitList(listParticipant.toMutableList())
        }
    }
    fun deleteParticipant(view: View?, string: String) {

    }
    fun timeToString(hourOfDay: Int, minute: Int): String {
        var time = "";
        if (hourOfDay < 10) time += "0";
        time += hourOfDay.toString()
        time += ":"
        if (minute < 10) time += "0"
        time += minute.toString()
        return time
    }
    fun dateToString(dayOfMonth: Int, month: Int, year: Int): String {
        var date = "";
        if (dayOfMonth < 10) date += "0";
        date += dayOfMonth.toString()
        date += "/"
        if (month < 9) date += "0"
        date += (month + 1).toString()
        date += "/"
        date += year.toString()
        return date
    }
}