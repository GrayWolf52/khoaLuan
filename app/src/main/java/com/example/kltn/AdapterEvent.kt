package com.example.kltn

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.models.StatusEvent

class EventAdapter(
    private val userId: Int,
    private val context: Context,
    private val onClick: (View?, EventItem) -> Unit,
    private val deleteEvent: (View?, EventItem) -> Unit,
    private val isAcceptEvent: (EventItem, Boolean, Int) -> Unit,
    private val changeStatusEvent: (EventItem, Int) -> Unit
) :
    ListAdapter<EventItem, EventAdapter.EventViewHolder>(EventDiffCallback) {

    class EventViewHolder(
        val userId: Int,
        private val context: Context,
        itemView: View,
        val onClick: (View?, EventItem) -> Unit,
        val deleteEvent: (View?, EventItem) -> Unit,
        val isAcceptEvent: (EventItem, Boolean, Int) -> Unit,
        val changeStatusEvent: (EventItem, Int) -> Unit
    ) :
        RecyclerView.ViewHolder(itemView) {
        private val lbEventDay: TextView = itemView.findViewById<TextView>(R.id.lbEventDay)
        private val lbEventMonth: TextView = itemView.findViewById<TextView>(R.id.lbEventMonth)
        private val lbEventStatus: TextView = itemView.findViewById<TextView>(R.id.lbEventStatus)
        private val lbEventName: TextView = itemView.findViewById<TextView>(R.id.lbEventName)
        private val txtLoimoi: TextView = itemView.findViewById(R.id.txtLoimoi)
        private val spinnerStatus: Spinner = itemView.findViewById(R.id.statusEvent)
        private val btnAccept: ImageButton = itemView.findViewById(R.id.accept)
        private val btnDeny: ImageButton = itemView.findViewById(R.id.deny)
        private val linearLayout: LinearLayoutCompat = itemView.findViewById(R.id.linear2)
        private lateinit var eventModel: EventItem

        init {
            itemView.setOnClickListener {
                eventModel.let {
                    if (eventModel.date != null) onClick(itemView, it)
                }
            }

            itemView.setOnLongClickListener {
                eventModel.let {
                    if (eventModel.date != null) deleteEvent(itemView, it)
                }
                true
            }
            btnAccept.setOnClickListener {
                eventModel.let {
                    isAcceptEvent(it, true, position)
                }
                linearLayout.visibility = View.GONE
                txtLoimoi.visibility = View.GONE
            }
            btnDeny.setOnClickListener {
                eventModel.let {
                    isAcceptEvent(it, false, position)
                }
            }
        }

        @SuppressLint("ResourceAsColor")
        fun bind(event: EventItem) {
            eventModel = event
            ArrayAdapter.createFromResource(
                context,
                R.array.list_status_event,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinnerStatus.adapter = adapter
            }
            spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    updateColor(statusEvent = position)
                    changeStatusEvent.invoke(event, position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
            if (event.date != null) {
                Log.d("bind", " bind = ${event.date.date}")
                lbEventDay.setText(event.date.date.toString())
                lbEventMonth.setText("Thg " + (event.date.month + 1).toString())
                lbEventName.setText(event.name)
                Log.d("TAG", "bind: $userId and ${event.userIdSendEvent} ")
                if (event.userIdSendEvent != userId) {
                    Log.d("TAG", "bind: true  ")
                    if (event.status == Status.ACCEPTED) {
                        linearLayout.visibility = View.GONE
                        spinnerStatus.visibility = View.VISIBLE
                        txtLoimoi.visibility = View.GONE
                    }
                    if (event.status == Status.NOT_YET_ACCEPT) {
                        linearLayout.visibility = View.VISIBLE
                        spinnerStatus.visibility = View.GONE
                        txtLoimoi.setText("Lời mời từ " + event.userNameSendEvent)
                    }
                }
                else {
                    Log.d("TAG", "bind: false ")
                    spinnerStatus.visibility = View.VISIBLE
                    linearLayout.visibility = View.GONE
                    txtLoimoi.visibility = View.GONE
                }
                spinnerStatus.setSelection(event.statusEvent)
                /*var color = when (event.statusEvent) {
                    StatusEvent.NEW -> R.color.blue
                    StatusEvent.DOING -> R.color.purple_700
                    StatusEvent.DONE -> R.color.green
                    else -> R.color.blue
                }
                lbEventStatus.background.setTint(ContextCompat.getColor(context, color))*/
                updateColor(event.statusEvent)
//                if (event.type == 1) {
//                    var drawable = lbEventStatus.background
//                    drawable = DrawableCompat.wrap(drawable)
//                    DrawableCompat.setTint(drawable, R.color.green)
//                }
//                else if (event.type == 2) {
//                    var drawable = lbEventStatus.background
//                    drawable = DrawableCompat.wrap(drawable)
//                    DrawableCompat.setTint(drawable, R.color.orange)
//                }
            }
        }

        fun updateColor(statusEvent: Int) {
            var color = when (statusEvent) {
                StatusEvent.NEW -> R.color.blue
                StatusEvent.DOING -> R.color.purple_700
                StatusEvent.DONE -> R.color.green
                else -> R.color.blue
            }
            lbEventStatus.background.setTint(ContextCompat.getColor(context, color))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(
            userId,
            context,
            view,
            onClick,
            deleteEvent,
            isAcceptEvent,
            changeStatusEvent
        )
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val day = getItem(position)
        holder.bind(day)
    }
}

object EventDiffCallback : DiffUtil.ItemCallback<EventItem>() {
    override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
        return (oldItem.type == newItem.type && oldItem.name == newItem.name && oldItem.date == newItem.date)
    }
}