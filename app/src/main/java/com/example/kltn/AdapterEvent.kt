package com.example.kltn

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlin.time.days

class EventAdapter(private val onClick: (View?, EventModel) -> Unit) :
    ListAdapter<EventModel, EventAdapter.EventViewHolder>(EventDiffCallback) {

    class EventViewHolder(itemView: View, val onClick: (View?, EventModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val lbEventDay: TextView = itemView.findViewById<TextView>(R.id.lbEventDay)
        private val lbEventMonth: TextView = itemView.findViewById<TextView>(R.id.lbEventMonth)
        private val lbEventStatus: TextView = itemView.findViewById<TextView>(R.id.lbEventStatus)
        private val lbEventName: TextView = itemView.findViewById<TextView>(R.id.lbEventName)
        private lateinit var eventModel: EventModel

        init {
            itemView.setOnClickListener {
                eventModel.let {
                    if (eventModel.date != null) onClick(itemView, it)
                }
            }
        }

        @SuppressLint("ResourceAsColor")
        fun bind(event: EventModel) {
            eventModel = event

            if (event.date != null) {
                lbEventDay.setText(event.date.date.toString())
                lbEventMonth.setText("Thg " + (event.date.month + 1).toString())
                lbEventName.setText(event.name)
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
        return EventViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val day = getItem(position)
        holder.bind(day)
    }
}
object EventDiffCallback : DiffUtil.ItemCallback<EventModel>() {
    override fun areItemsTheSame(oldItem: EventModel, newItem: EventModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: EventModel, newItem: EventModel): Boolean {
        return (oldItem.type == newItem.type && oldItem.name == newItem.name)
    }
}