package com.example.kltn

import android.R.attr.data
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.models.UserModel
import com.example.kltn.services.EventService
import com.example.kltn.services.UserGroupService
import com.example.kltn.services.UserService
import java.util.*


class ActivityEditEvent : AppCompatActivity() {
    private lateinit var btnEditEventBack: TextView
    private lateinit var btnSaveEvent: Button
    private lateinit var btnEditEventEdit: TextView
    private lateinit var btnEditEventCancel: Button
    private lateinit var txtEditEventTitle: TextView
    private lateinit var txtEditEventDescription: TextView
    private lateinit var txtEditEventStartTime: TextView
    private lateinit var txtEditEventStartDate: TextView
    private lateinit var txtEditEventEndTime: TextView
    private lateinit var txtEditEventEndDate: TextView
    private lateinit var txtEditEventParticipant: AutoCompleteTextView
    private lateinit var recyclerViewParticipant: RecyclerView
    private lateinit var spPosition: Spinner
    private lateinit var selectedParticipantAdapter: AdapterParticipant
    private lateinit var spnEditEventLoop: Spinner
    private lateinit var chkEditEventLoop: CheckBox
    private lateinit var calendarStart: Calendar
    private lateinit var calendarEnd: Calendar
    private var listParticipant = ArrayList<UserModel>()
    private val _participants: ArrayList<UserModel> = ArrayList<UserModel>()
    private val _participants2: ArrayList<UserModel> = ArrayList<UserModel>()
    private var participants: ArrayList<String> = ArrayList<String>()
    private val listLoopType = ArrayList<String>()
    private var userId: Int = 0
    private var eventId: Int = 0
    private lateinit var loopAdaper: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        var bundle = intent.extras
        userId = bundle!!.getInt("UserId")
        eventId = bundle!!.getInt("EventId")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editevent)
        btnSaveEvent = findViewById(R.id.btnSaveEvent)
        btnEditEventCancel = findViewById(R.id.btnEditEventCancel)
        btnEditEventEdit = findViewById(R.id.btnEditEventEdit)
        txtEditEventTitle = findViewById(R.id.txtEditEventTitle)
        txtEditEventDescription = findViewById(R.id.txtEditEventDescription)
        btnEditEventBack = findViewById(R.id.btnStaffScheduleBack)
        txtEditEventStartTime = findViewById(R.id.txtEditEventStartTime)
        txtEditEventStartDate = findViewById(R.id.txtEditEventStartDate)
        txtEditEventEndTime = findViewById(R.id.txtEditEventEndTime)
        txtEditEventEndDate = findViewById(R.id.txtEditEventEndDate)
        txtEditEventParticipant = findViewById(R.id.txtEditEventParticipant)
        recyclerViewParticipant = findViewById(R.id.recyclerViewParticipant)
        spnEditEventLoop = findViewById(R.id.spnEditEventLoop)
        chkEditEventLoop = findViewById(R.id.chkEditEventLoop)
        btnEditEventBack.setOnClickListener {
            finish()
        }
        calendarStart = Calendar.getInstance()
        calendarEnd = Calendar.getInstance()
        calendarEnd.add(Calendar.HOUR, 1)
        val startDatePicker = object: DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                txtEditEventStartDate.text = dateToString(dayOfMonth, month, year)
                calendarStart.set(Calendar.YEAR, year)
                calendarStart.set(Calendar.MONTH, month)
                calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                refreshLoopType()
            }
        }
        val endDatePicker = object: DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                txtEditEventEndDate.text = dateToString(dayOfMonth, month, year)
                calendarEnd.set(Calendar.YEAR, year)
                calendarEnd.set(Calendar.MONTH, month)
                calendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
        }
        txtEditEventStartDate.setOnClickListener {
            val startDatePickerDialog = DatePickerDialog(this, startDatePicker, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH))
            startDatePickerDialog.show()
        }
        txtEditEventEndDate.setOnClickListener {
            val endDatePickerDialog = DatePickerDialog(this, endDatePicker, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH))
            endDatePickerDialog.show()
        }

        val startTimePicker = object: TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                txtEditEventStartTime.text = timeToString(hourOfDay, minute)
                calendarStart.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendarStart.set(Calendar.MINUTE, minute)
                refreshLoopType();
            }
        }
        val endTimePicker = object: TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                txtEditEventEndTime.text = timeToString(hourOfDay, minute)
                calendarEnd.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendarEnd.set(Calendar.MINUTE, minute)
            }
        }
        txtEditEventStartTime.setOnClickListener {
            val startTimePickerDialog = TimePickerDialog(this, startTimePicker, calendarStart.get(Calendar.HOUR_OF_DAY), calendarStart.get(Calendar.MINUTE), true)
            startTimePickerDialog.show()
        }
        txtEditEventEndTime.setOnClickListener {
            val endTimePickerDialog = TimePickerDialog(this, endTimePicker, calendarEnd.get(Calendar.HOUR_OF_DAY), calendarEnd.get(Calendar.MINUTE), true)
            endTimePickerDialog.show()
        }

        txtEditEventStartTime.text = timeToString(calendarStart.get(Calendar.HOUR_OF_DAY), calendarStart.get(Calendar.MINUTE))
        txtEditEventStartDate.text = dateToString(calendarStart.get(Calendar.DAY_OF_MONTH), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.YEAR))

        txtEditEventEndTime.text = timeToString(calendarEnd.get(Calendar.HOUR_OF_DAY), calendarEnd.get(Calendar.MINUTE))
        txtEditEventEndDate.text = dateToString(calendarEnd.get(Calendar.DAY_OF_MONTH), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.YEAR))

        selectedParticipantAdapter = AdapterParticipant { view, participant -> deleteParticipant(view, participant) }
        recyclerViewParticipant.adapter = selectedParticipantAdapter
        val participantAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, participants)
        txtEditEventParticipant.threshold = 2
        txtEditEventParticipant.setAdapter(participantAdapter)
        txtEditEventParticipant.setOnItemClickListener {parent, view, position, id ->
            listParticipant.add(_participants2[position])
            selectedParticipantAdapter.submitList(listParticipant.toMutableList())
            txtEditEventParticipant.setText("")
            txtEditEventParticipant.clearFocus()
        }
        txtEditEventParticipant.doBeforeTextChanged { text, start, count, after ->
            _participants2.clear()
            for (item in _participants) _participants2.add(item)
        }
        txtEditEventParticipant.doAfterTextChanged {
            if (it.toString().length >= txtEditEventParticipant.threshold) {
                var selectedParticipant = ArrayList<Int>()
                for (item in listParticipant) {
                    if (selectedParticipant.contains(item.id)) continue
                    selectedParticipant.add(item.id)
                }
                var resultSearchUser = UserService.SearchWithout(it.toString(),selectedParticipant)
                if (resultSearchUser.first.length > 0) {
                    Toast.makeText(this, resultSearchUser.first, Toast.LENGTH_SHORT).show()
                }
                else if (resultSearchUser.second != null) {
                    for (item in resultSearchUser.second!!) _participants.add(item)
                    participantAdapter.clear()
                    for (user in _participants) {
                        participantAdapter.add(user.username + " - " + user.lastname + " " + user.firstname)
                    }
                }
                participantAdapter.notifyDataSetChanged()
            }
        }

        loopAdaper = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listLoopType)
        spnEditEventLoop.adapter = loopAdaper

        chkEditEventLoop.setOnClickListener {
            if (chkEditEventLoop.isChecked) spnEditEventLoop.visibility = View.VISIBLE
            else spnEditEventLoop.visibility = View.GONE
        }
        spPosition = findViewById(R.id.spPosition)
        btnSaveEvent.setOnClickListener {
            var recurrenceType = 0
            if (chkEditEventLoop.isChecked) recurrenceType = (spnEditEventLoop.selectedItemPosition + 1)
            Thread({
                var msg = EventService.update(eventId, txtEditEventTitle.text.toString(), txtEditEventDescription.text.toString(), calendarStart.time, calendarEnd.time, recurrenceType, (spPosition.selectedItem as Item).id, listParticipant.map{ it.id}, userId)
                if (msg.length > 0) {
                    runOnUiThread(Runnable {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    })
                }
                else {
                    runOnUiThread(Runnable {
                        changeUIStatus(false)
                    })
                }
            }).start()
        }
        btnEditEventEdit.setOnClickListener {
            changeUIStatus(true)
        }
        btnEditEventCancel.setOnClickListener {
            loadEvent()
        }
        loadUserGroup()
        if (eventId > 0) loadEvent()
        else refreshLoopType()
    }
    fun refreshLoopType() {
        listLoopType.clear()
        var dayOfWeek = calendarStart.get(Calendar.DAY_OF_WEEK)
        listLoopType.add("Mỗi " + timeToString(calendarStart.get(Calendar.HOUR_OF_DAY), calendarStart.get(Calendar.MINUTE)) + " hằng ngày")
        if (dayOfWeek == 1) listLoopType.add("Mỗi chủ nhật hằng tuần")
        else listLoopType.add("Mỗi thứ " + dayOfWeek.toString() + " hằng tuần")
        var dayOfMonth = calendarStart.get(Calendar.DAY_OF_MONTH)
        listLoopType.add("Mỗi ngày " + dayOfMonth + " hằng tháng")
        loopAdaper.notifyDataSetChanged()
    }
    fun deleteParticipant(view: View?, participant: UserModel) {
        var newList = listParticipant.filter { !(it.id == participant.id) }
        listParticipant.clear()
        for (item in newList) listParticipant.add(item)
        selectedParticipantAdapter.submitList(listParticipant.toMutableList())
    }
    fun timeToString(hourOfDay: Int, minute: Int): String {
        var time = "";
        if (hourOfDay < 10) time += "0";
        time += hourOfDay.toString()
        time += "h"
        if (minute < 10) time += "0"
        time += minute.toString()
        return time
    }
    fun dateToString(dayOfMonth: Int, month: Int, year: Int): String {
        var dateText = "";
        val date = Date(year - 1900, month, dayOfMonth)
        val cal = Calendar.getInstance()
        cal.setTime(date)
        var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == 1) dateText = "Chủ nhật, "
        else dateText = "Thứ " + dayOfWeek.toString() + ", "
        if (dayOfMonth < 10) dateText += "0";
        dateText += dayOfMonth.toString()
        dateText += "/"
        if (month < 9) dateText += "0"
        dateText += (month + 1).toString()
        dateText += "/"
        dateText += year.toString()
        return dateText
    }
    fun loadUserGroup() {
        Thread({
            var resultUserGroup = UserGroupService.GetForUser(userId)
            if (resultUserGroup.first.length > 0) {
                Toast.makeText(this, resultUserGroup.first, Toast.LENGTH_SHORT).show()
            }
            else {
                try {
                    var positions = ArrayList<Item>()
                    for (userGroup in resultUserGroup.second!!) {
                        positions.add(
                            Item(
                                userGroup.groupId,
                                userGroup.role?.name + " - " + userGroup.group?.name
                            )
                        )
                    }
                    val dataAdapter =
                        ArrayAdapter<Item>(this, android.R.layout.simple_spinner_item, positions)
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    runOnUiThread(Runnable {
                        spPosition.setAdapter(dataAdapter)
                    })
                }
                catch (ex: Exception) {
                    var a = ex
                }
            }
        }).start()

    }
    fun changeUIStatus(isEdit: Boolean) {
        txtEditEventTitle.isEnabled = false
        txtEditEventDescription.isEnabled = false
        txtEditEventStartTime.isEnabled = false
        txtEditEventStartDate.isEnabled = false
        txtEditEventEndTime.isEnabled = false
        txtEditEventEndDate.isEnabled = false

        if (isEdit) {
            btnEditEventEdit.visibility = View.GONE
            btnSaveEvent.visibility = View.VISIBLE
            btnEditEventEdit.visibility = View.VISIBLE
        }
        else {
            btnSaveEvent.visibility = View.VISIBLE
            btnEditEventBack.visibility = View.GONE
            btnEditEventEdit.visibility = View.VISIBLE
        }
    }
    fun loadEvent() {
        var resultEvent = EventService.getById(eventId)
        if (resultEvent.first.length > 0) {
            Toast.makeText(this, resultEvent.first, Toast.LENGTH_SHORT).show()
            finish()
        }
        else {
            var event = resultEvent.second
            txtEditEventTitle.text = event?.title
            txtEditEventDescription.text = event?.description
            calendarStart.time = event?.startDate
            calendarEnd.time = event?.endDate
            if (event?.loopType!! > 0) {
                chkEditEventLoop.isChecked = true
                spnEditEventLoop.visibility = View.VISIBLE
                spnEditEventLoop.setSelection(event?.loopType - 1)
            }
            else {
                chkEditEventLoop.isChecked = false
                spnEditEventLoop.visibility = View.GONE
            }
        }
        changeUIStatus(false)
    }
}