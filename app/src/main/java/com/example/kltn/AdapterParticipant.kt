package com.example.kltn

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class AdapterParticipant (private val onClick: (View?, String) -> Unit) :
    ListAdapter<String, AdapterParticipant.ParticipantViewHolder>(ParticipantDiffCallback) {

    class ParticipantViewHolder(itemView: View, val onClick: (View?, String) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private lateinit var _participant: String

        init {
            itemView.setOnClickListener {
                _participant.let {
                }
            }
        }

        @SuppressLint("ResourceAsColor")
        fun bind(participant: String) {
            _participant = participant
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participant, parent, false)
        return ParticipantViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val participant = getItem(position)
        holder.bind(participant)
    }
}
object ParticipantDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return false
    }
}