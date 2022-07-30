package com.example.kltn

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(
    private val onClick: (View?, EventItem) -> Unit,
    private val deleteEvent: (View?, EventItem) -> Unit,
    private val isAcceptEvent: (EventItem, Boolean) -> Unit
) :
    ListAdapter<EventItem, EventAdapter.EventViewHolder>(EventDiffCallback) {

    class EventViewHolder(
        itemView: View,
        val onClick: (View?, EventItem) -> Unit,
        val deleteEvent: (View?, EventItem) -> Unit,
        val isAcceptEvent: (EventItem, Boolean) -> Unit
    ) :
        RecyclerView.ViewHolder(itemView) {
        private val lbEventDay: TextView = itemView.findViewById<TextView>(R.id.lbEventDay)
        private val lbEventMonth: TextView = itemView.findViewById<TextView>(R.id.lbEventMonth)
        private val lbEventStatus: TextView = itemView.findViewById<TextView>(R.id.lbEventStatus)
        private val lbEventName: TextView = itemView.findViewById<TextView>(R.id.lbEventName)
        private val txtLoimoi: TextView = itemView.findViewById(R.id.txtLoimoi)
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
                    isAcceptEvent(it, true)
                }
            }
            btnDeny.setOnClickListener {
                eventModel.let {
                    isAcceptEvent(it, false)
                }
            }
        }

        @SuppressLint("ResourceAsColor")
        fun bind(event: EventItem) {
            eventModel = event

            if (event.date != null) {
                Log.d("bind", " bind = ${event.date.date}")
                lbEventDay.setText(event.date.date.toString())
                lbEventMonth.setText("Thg " + (event.date.month + 1).toString())
                lbEventName.setText(event.name)

                if (event.status == Status.ACCEPTED) {
                    linearLayout.visibility = View.GONE
                    txtLoimoi.visibility = View.GONE
                }
                if (event.status == Status.NOT_YET_ACCEPT) {
                    linearLayout.visibility = View.VISIBLE
                    txtLoimoi.setText("Lời mời từ ")
                }
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view, onClick, deleteEvent, isAcceptEvent)
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