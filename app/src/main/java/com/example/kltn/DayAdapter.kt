package com.example.kltn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class DayAdapter(private val onClick: (View?, DayModel) -> Unit) :
    ListAdapter<DayModel, DayAdapter.DayViewHolder>(DayDiffCallback) {

    class DayViewHolder(itemView: View, val onClick: (View?, DayModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val lbDay: TextView = itemView.findViewById<TextView>(R.id.lbDay)
        private lateinit var dayModel: DayModel

        init {
            itemView.setOnClickListener {
                dayModel.let {
                    onClick(itemView, it)
                }
            }
        }

        fun bind(day: DayModel) {
            dayModel = day

            if (day.date != null)
                lbDay.setText(day.date?.date.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.day_item, parent, false)
        return DayViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = getItem(position)
        holder.bind(day)
    }
}
object DayDiffCallback : DiffUtil.ItemCallback<DayModel>() {
    override fun areItemsTheSame(oldItem: DayModel, newItem: DayModel): Boolean {
        return false
        if (oldItem == null && newItem == null) return true
        if (oldItem == null || newItem == null) return false
        return oldItem!!.date == newItem!!.date
    }

    override fun areContentsTheSame(oldItem: DayModel, newItem: DayModel): Boolean {
        return true
    }
}