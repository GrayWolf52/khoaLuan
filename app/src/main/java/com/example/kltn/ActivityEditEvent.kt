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
import com.example.kltn.models.DataSuggest
import com.example.kltn.models.UserModel
import com.example.kltn.services.EventService
import com.example.kltn.services.GroupService
import com.example.kltn.services.UserGroupService
import com.example.kltn.services.UserService
import com.example.kltn.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

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
    private lateinit var selectedParticipantAdapter: AdapterParticipant
    private lateinit var spnEditEventLoop: Spinner
    private lateinit var chkEditEventLoop: CheckBox
    private lateinit var calendarStart: Calendar
    private lateinit var calendarEnd: Calendar
    private var listParticipant = ArrayList<DataSuggest>()
    private var listIdParticipanted = ArrayList<Int>()
    private val _participants: ArrayList<DataSuggest> = ArrayList<DataSuggest>()
    private val _participants2: ArrayList<DataSuggest> = ArrayList<DataSuggest>()
    private val listData = mutableListOf<UserModel>()
    private var participants: ArrayList<String> = ArrayList<String>()
    private val listLoopType = ArrayList<String>()
    private var userId: Int = 0
    private var eventId: Int = 0
    private var groupId: Int = -1
    private var isEventGroup = false
    private lateinit var loopAdaper: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        var bundle = intent.extras
        userId = bundle!!.getInt(Constants.USER_ID)
        eventId = bundle!!.getInt(Constants.EVENT_ID)
        groupId = bundle?.getInt(Constants.GROUD_ID)
        isEventGroup = bundle?.getBoolean(Constants.IS_ADD_EVENT_GROUP)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editevent)
        initView()
        selectedParticipantAdapter =
            AdapterParticipant { view, participant ->
                Log.d("TAG", "onCreate:AdapterParticipant ")
                deleteParticipant(view, participant)
            }
        recyclerViewParticipant.adapter = selectedParticipantAdapter
        val participantAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.select_dialog_item, participants)
        txtEditEventParticipant.threshold = 2
        txtEditEventParticipant.setAdapter(participantAdapter)
        txtEditEventParticipant.setOnItemClickListener { _, _, position, _ ->
            Log.d("TAG", "onCreate:txtEditEventParticipant  setOnItemClickListener")
            listParticipant.add(_participants2[position])
            val listValue = mutableListOf<UserModel>()
            for (user in  _participants2[position].users){
                for (it in listData){
                    if (user.id != it.id) {
                        Log.d("TAG", "onCreate: listIdParticipanted $it usser id = ${user.id}")
                        listValue.add(user)
                    }
                }
                listData.addAll(listValue)
            }

            Log.d("TAG", "onCreate: listData size = ${listData.size}")
            selectedParticipantAdapter.submitList(listData)
            selectedParticipantAdapter.notifyDataSetChanged()
            txtEditEventParticipant.setText("")
            txtEditEventParticipant.clearFocus()
        }
        txtEditEventParticipant.doBeforeTextChanged { _, _, _, _ ->
            _participants2.clear()
            for (item in _participants) _participants2.add(item)
        }
        txtEditEventParticipant.doAfterTextChanged {
            if (it.toString().length >= txtEditEventParticipant.threshold) {
                var selectedParticipant = ArrayList<Int>()
                for (item in listParticipant) {
                    item.users.forEach {
                        if (!selectedParticipant.contains(it.id)) {
                            selectedParticipant.add(it.id)
                        }
                    }
                }
                GlobalScope.launch(Dispatchers.IO) {
                    // do background task
                    var resultSearchUser =
                        UserService.SearchWithout(it.toString(), selectedParticipant)
                    withContext(Dispatchers.Main) {
                        // update UI
                        if (resultSearchUser.first.isNotEmpty()) {
                            Toast.makeText(
                                this@ActivityEditEvent,
                                resultSearchUser.first,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (resultSearchUser.second != null) {
                            for (item in resultSearchUser.second!!) _participants.add(item)
                            participantAdapter.clear()
                            for (user in resultSearchUser.second!!) {
                                if (user.type == 2) participantAdapter.add(user.shortname + " - " + user.fullname) else
                                    user.users.forEach {
                                        participantAdapter.add(it.username + " - " + it.lastname + " " + it.firstname)
                                    }
                            }
                        }
                        participantAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        loopAdaper = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listLoopType)
        spnEditEventLoop.adapter = loopAdaper

        chkEditEventLoop.setOnClickListener {
            if (chkEditEventLoop.isChecked) spnEditEventLoop.visibility = View.VISIBLE
            else spnEditEventLoop.visibility = View.GONE
        }
        btnSaveEvent.setOnClickListener {
            var recurrenceType = 0
            if (chkEditEventLoop.isChecked) recurrenceType =
                (spnEditEventLoop.selectedItemPosition + 1)
            Thread {
                Log.d("calendarStart", " calendarStart = ${calendarStart.time}")
                Log.d("calendarStart", " calendarEnd = ${calendarEnd.time}")
                val listId = mutableListOf<Int>()
                listParticipant.forEach {
                    it.users.forEach {
                        for (id in listIdParticipanted) {
                            if (it.id != id)
                                listId.add(it.id)
                        }
                    }
                }
                var msg = EventService.update(
                    eventId = eventId,
                    title = txtEditEventTitle.text.toString(),
                    description = txtEditEventDescription.text.toString(),
                    startTime = calendarStart.time,
                    endTime = calendarEnd.time,
                    recurrenceType = recurrenceType,
                    groupId = groupId,
                    creatorId = userId,
                    participants = listId,
                )
                if (msg.isNotEmpty()) {
                    runOnUiThread {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        changeUIStatus(false)
                    }
                }
            }.start()
        }
        btnEditEventEdit.setOnClickListener {
            changeUIStatus(true)
        }
        btnEditEventCancel.setOnClickListener {
            runOnUiThread {
                loadEvent()
                changeUIStatus(false)
            }
        }
        loadUserGroup()
        if (eventId > 0) loadEvent()
        else refreshLoopType()
    }

    private fun refreshLoopType() {
        listLoopType.clear()
        var dayOfWeek = calendarStart.get(Calendar.DAY_OF_WEEK)
        listLoopType.add(
            "Mỗi " + timeToString(
                calendarStart.get(Calendar.HOUR_OF_DAY),
                calendarStart.get(Calendar.MINUTE)
            ) + " hằng ngày"
        )
        if (dayOfWeek == 1) listLoopType.add("Mỗi chủ nhật hằng tuần")
        else listLoopType.add("Mỗi thứ $dayOfWeek hằng tuần")
        var dayOfMonth = calendarStart.get(Calendar.DAY_OF_MONTH)
        listLoopType.add("Mỗi ngày $dayOfMonth hằng tháng")
        loopAdaper.notifyDataSetChanged()
    }

    private fun initView() {
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
        val startDatePicker =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

                txtEditEventStartDate.text = dateToString(dayOfMonth, month, year)
                calendarStart.set(Calendar.YEAR, year)
                calendarStart.set(Calendar.MONTH, month)
                calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                refreshLoopType()
            }
        val endDatePicker =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                txtEditEventEndDate.text = dateToString(dayOfMonth, month, year)
                calendarEnd.set(Calendar.YEAR, year)
                calendarEnd.set(Calendar.MONTH, month)
                calendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
        txtEditEventStartDate.setOnClickListener {
            val startDatePickerDialog = DatePickerDialog(
                this,
                startDatePicker,
                calendarStart.get(Calendar.YEAR),
                calendarStart.get(Calendar.MONTH),
                calendarStart.get(Calendar.DAY_OF_MONTH)
            )
            startDatePickerDialog.show()
        }
        txtEditEventEndDate.setOnClickListener {
            val endDatePickerDialog = DatePickerDialog(
                this,
                endDatePicker,
                calendarEnd.get(Calendar.YEAR),
                calendarEnd.get(Calendar.MONTH),
                calendarEnd.get(Calendar.DAY_OF_MONTH)
            )
            endDatePickerDialog.show()
        }

        val startTimePicker =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                txtEditEventStartTime.text = timeToString(hourOfDay, minute)
                calendarStart.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendarStart.set(Calendar.MINUTE, minute)
                refreshLoopType();
            }
        val endTimePicker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            txtEditEventEndTime.text = timeToString(hourOfDay, minute)
            calendarEnd.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendarEnd.set(Calendar.MINUTE, minute)
        }
        txtEditEventStartTime.setOnClickListener {
            val startTimePickerDialog = TimePickerDialog(
                this,
                startTimePicker,
                calendarStart.get(Calendar.HOUR_OF_DAY),
                calendarStart.get(Calendar.MINUTE),
                true
            )
            startTimePickerDialog.show()
        }
        txtEditEventEndTime.setOnClickListener {
            val endTimePickerDialog = TimePickerDialog(
                this,
                endTimePicker,
                calendarEnd.get(Calendar.HOUR_OF_DAY),
                calendarEnd.get(Calendar.MINUTE),
                true
            )
            endTimePickerDialog.show()
        }

        txtEditEventStartTime.text = timeToString(
            calendarStart.get(Calendar.HOUR_OF_DAY),
            calendarStart.get(Calendar.MINUTE)
        )
        txtEditEventStartDate.text = dateToString(
            calendarStart.get(Calendar.DAY_OF_MONTH),
            calendarStart.get(Calendar.MONTH),
            calendarStart.get(Calendar.YEAR)
        )

        txtEditEventEndTime.text =
            timeToString(calendarEnd.get(Calendar.HOUR_OF_DAY), calendarEnd.get(Calendar.MINUTE))
        txtEditEventEndDate.text = dateToString(
            calendarEnd.get(Calendar.DAY_OF_MONTH),
            calendarEnd.get(Calendar.MONTH),
            calendarEnd.get(Calendar.YEAR)
        )
    }

    private fun deleteParticipant(view: View?, participant: UserModel) {
        Log.d("TAG", "deleteParticipant: ")
        var newList = listData.findLast {
            Log.d("TAG", "deleteParticipant: ${it.id} and id particapant ${participant.id}")
            (it.id == participant.id)
        }
        listData.remove(newList)
        listData.clear()
        Log.d("TAG", "deleteParticipant:listData ${listData.size}")
        selectedParticipantAdapter.submitList(listData)
        /*   selectedParticipantAdapter.notifyDataSetChanged()*/
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

    private fun loadUserGroup() {
        GlobalScope.launch(Dispatchers.IO) {
            var resultUserGroup = UserGroupService.GetForUser(userId)
            withContext(Dispatchers.Main) {
                if (resultUserGroup.first.isNotEmpty()) {
                    Toast.makeText(
                        this@ActivityEditEvent,
                        resultUserGroup.first,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    try {
                        var positions = ArrayList<Item>()
                        if (!isEventGroup) {
                            Log.d(
                                "isEventGroup",
                                "isEventGroup = $isEventGroup and groupId = $groupId"
                            )
                            for (userGroup in resultUserGroup.second!!) {
                                positions.add(
                                    Item(
                                        userGroup.groupId,
                                        userGroup.role?.name + " - " + userGroup.group?.name
                                    )
                                )
                            }
                        } else {
                            Log.d(
                                "isEventGroup",
                                "isEventGroup = $isEventGroup and groupId = $groupId"
                            )
                            resultUserGroup.second?.let { result ->
                                Log.d("resultUserGroup", " resultUserGroup = ${result.size}")
                                result.findLast {
                                    it.groupId == groupId
                                }?.let {
                                    Log.d(
                                        "isEventGroup",
                                        "isEventGroup = $isEventGroup and name = ${it.group?.name}"
                                    )
                                    positions.add(
                                        Item(
                                            it.groupId,
                                            it.role?.name + " - " + it.group?.name
                                        )
                                    )
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        var a = ex
                    }
                }
            }
        }

    }

    private fun changeUIStatus(isEdit: Boolean) {
        txtEditEventTitle.isEnabled = isEdit
        txtEditEventDescription.isEnabled = isEdit
        txtEditEventStartTime.isEnabled = isEdit
        txtEditEventStartDate.isEnabled = isEdit
        txtEditEventEndTime.isEnabled = isEdit
        txtEditEventEndDate.isEnabled = isEdit
        chkEditEventLoop.isEnabled = isEdit
        if (isEdit) {
            btnSaveEvent.visibility = View.VISIBLE
            btnEditEventCancel.visibility = View.VISIBLE
            btnEditEventEdit.visibility = View.GONE
            txtEditEventParticipant.visibility = View.VISIBLE
        } else {
            btnSaveEvent.visibility = View.GONE
            btnEditEventCancel.visibility = View.GONE
            btnEditEventEdit.visibility = View.VISIBLE
            txtEditEventParticipant.visibility = View.GONE
        }
    }

    private fun loadEvent() {
        Log.d("loadEvent", "loadEvent")
        Thread {
            var resultEvent = EventService.getById(eventId)
            resultEvent.second?.participants?.also {
                listIdParticipanted.clear()
                it.forEach {
                    Log.d("TAG", "loadEvent: user name = ${it.username}")
                    listData.add(it)
                    listIdParticipanted.add(it.id)
                }
                selectedParticipantAdapter.submitList(listData)
            }
            if (resultEvent.first.isNotEmpty()) {
                runOnUiThread {
                    Toast.makeText(this, resultEvent.first, Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                var event = resultEvent.second
                txtEditEventTitle.text = event?.title
                txtEditEventDescription.text = event?.description
                calendarStart.time = event?.startTime
                txtEditEventStartTime.text = timeToString(
                    calendarStart.get(Calendar.HOUR_OF_DAY),
                    calendarStart.get(Calendar.MINUTE)
                )
                txtEditEventStartDate.text = dateToString(
                    calendarStart.get(Calendar.DAY_OF_MONTH),
                    calendarStart.get(Calendar.MONTH),
                    calendarStart.get(Calendar.YEAR)
                )
                calendarEnd.time = event?.endTime
                txtEditEventEndTime.text = timeToString(
                    calendarEnd.get(Calendar.HOUR_OF_DAY),
                    calendarEnd.get(Calendar.MINUTE)
                )
                txtEditEventEndDate.text = dateToString(
                    calendarEnd.get(Calendar.DAY_OF_MONTH),
                    calendarEnd.get(Calendar.MONTH),
                    calendarEnd.get(Calendar.YEAR)
                )
                refreshLoopType()
                if (event?.loopType!! > 0) {
                    chkEditEventLoop.isChecked = true
                    spnEditEventLoop.visibility = View.VISIBLE
                    spnEditEventLoop.setSelection(event?.loopType - 1)
                } else {
                    chkEditEventLoop.isChecked = false
                    spnEditEventLoop.visibility = View.GONE
                }

            }
            runOnUiThread {
                changeUIStatus(false)
            }
        }.start()
    }
}