package com.example.kltn

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.services.EventService
import com.example.kltn.services.UserGroupService
import com.example.kltn.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class FragmentHome : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var recyclerViewEvent: RecyclerView? = null
    private lateinit var dayViewModel: DayViewModel
    private var lbMonth: TextView? = null
    private lateinit var btnBack: TextView
    private lateinit var btnNext: TextView
    private lateinit var btnAddEvent: TextView

    /*  private lateinit var spView: Spinner*/
    lateinit var dayAdapter: DayAdapter
    lateinit var eventAdapter: EventAdapter
    private val calendar = Calendar.getInstance()
    private var userId = -1
    private var groupId = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var bundle = arguments
        if (bundle != null) {
            userId = bundle!!.getInt("UserId")
        }

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
            b.putInt("GroupId", 0)
            intent.putExtras(b)
            startActivity(intent)
        }
        dayAdapter = DayAdapter { view, day -> adapterDayOnClick(view, day) }
        eventAdapter =
            EventAdapter({ view, event -> adapterEventOnClick(view, event) }, { view, event ->
                deleteEvent(event.id)
            })
        recyclerView!!.adapter = dayAdapter
        recyclerViewEvent!!.adapter = eventAdapter
        dayViewModel = ViewModelProviders.of(this, DayViewModelFactory(context as Context))
            .get(DayViewModel::class.java)
        dayViewModel.listDay.observe(this.requireActivity(), Observer {
            it?.let {
                dayAdapter.submitList(it as MutableList<DayModel>)
            }
        })
        dayViewModel.listEvent.observe(this.requireActivity(), Observer {
            Log.d("listEvent", " listEvent = ${it.size}")
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
        //   loadUserGroup()
        refreshEvent()
        return view
    }

    private fun adapterEventOnClick(view: View?, event: EventItem) {
        var intent = Intent(context, ActivityEditEvent::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        intent.putExtra(Constants.EVENT_ID, event.id)
        intent.putExtra(Constants.GROUD_ID, event.groupId)
        startActivity(intent)
    }

    private fun adapterDayOnClick(view: View?, day: DayModel) {
        if (day.date == null) return;
    }

    fun refreshEvent() {
        var month = calendar.get(Calendar.MONTH) + 1
        var year = calendar.get(Calendar.YEAR)
        activity?.runOnUiThread{
            lbMonth!!.setText("$month / $year")
        }
        Thread {
            Log.d("groupid", "goupdId = $groupId")
            //  api trả về chỉ có 1 item nếu truyền vào là userid mà không là số 0
            dayViewModel.load(userId, groupId, month, year)
        }.start()
    }

    override fun onResume() {
        super.onResume()
        Log.d("FragmentHome", "hahahahhh")
        refreshEvent()
    }

    private fun loadUserGroup() {
        Thread {
            var resultUserGroup = UserGroupService.GetForUser(userId)
            if (resultUserGroup.first.isNotEmpty()) {
                Toast.makeText(context, resultUserGroup.first, Toast.LENGTH_SHORT).show()
            } else {
                try {
                    var positions = ArrayList<Item>()
                    positions.add(Item(0, "Tất cả"))
                    for (userGroup in resultUserGroup.second!!) {
                        positions.add(
                            Item(
                                userGroup.groupId,
                                userGroup.group.let {
                                    if (it == null) ""
                                    it!!.name
                                }
                            )
                        )
                    }
                    /*   val dataAdapter =
                           ArrayAdapter<Item>(
                               requireContext(),
                               android.R.layout.simple_spinner_item,
                               positions
                           )
                       dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)*/
                    /* activity?.runOnUiThread(Runnable {
                         spView.adapter = dataAdapter
                     })*/
                } catch (ex: Exception) {
                    var a = ex
                }
            }
        }.start()

    }


    private fun deleteEvent(idEvent: Int) {
        Log.d("TAG", "deleteGroup: id event = $idEvent")
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("TAG", "deleteGroup: id event = $idEvent")
            activity?.let {
                Log.d("TAG", "deleteGroup1111:  id event = $idEvent")
                val builder = AlertDialog.Builder(it)
                builder.setTitle("Xóa event")
                    .setMessage("Bạn có chắc chắn muốn xóa event này không?")
                    .setPositiveButton("Có") { _, _ ->
                        GlobalScope.launch(Dispatchers.IO) {
                            // do background task
                            var result = EventService.deleteEvent(idEvent)

                            withContext(Dispatchers.Main) {
                                // update UI

                                Toast.makeText(context, result.first, Toast.LENGTH_LONG).show()
                            }
                            refreshEvent()
                        }
                    }.setNegativeButton("Không") { dialog, _ ->
                        dialog.cancel()
                    }
                builder.create()
                builder.show()
            }
        }
    }
}