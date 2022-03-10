package com.example.kltn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class DayAdapter(private val onClick: (View?, DayModel) -> Unit) :
    ListAdapter<DayModel, DayAdapter.DayViewHolder>(DayDiffCallback) {

    class DayViewHolder(itemView: View, val onClick: (View?, DayModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val lbDay: TextView = itemView.findViewById<TextView>(R.id.lbDay)
        private val lbStatus1: TextView = itemView.findViewById<TextView>(R.id.lbStatus1)
        private val lbStatus2: TextView = itemView.findViewById<TextView>(R.id.lbStatus2)
        private lateinit var dayModel: DayModel

        init {
            itemView.setOnClickListener {
                dayModel.let {
                    if (dayModel.date != null) onClick(itemView, it)
                }
            }
        }

        fun bind(day: DayModel) {
            dayModel = day

            if (day.date != null)
                lbDay.setText(day.date?.date.toString())
            else
                lbDay.setText("")
            if (dayModel.status1) lbStatus1.visibility = View.VISIBLE
            else lbStatus1.visibility = View.GONE

            if (dayModel.status2) lbStatus2.visibility = View.VISIBLE
            else lbStatus2.visibility = View.GONE


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day, parent, false)
        return DayViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = getItem(position)
        holder.bind(day)
    }
}
object DayDiffCallback : DiffUtil.ItemCallback<DayModel>() {
    override fun areItemsTheSame(oldItem: DayModel, newItem: DayModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DayModel, newItem: DayModel): Boolean {
        return (oldItem.status1 == newItem.status1 && oldItem.status2 == newItem.status2)
    }
}