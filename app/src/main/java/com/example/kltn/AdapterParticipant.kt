package com.example.kltn

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.models.UserModel

class AdapterParticipant (private val onClick: (View?, UserModel) -> Unit) :
    ListAdapter<UserModel, AdapterParticipant.ParticipantViewHolder>(ParticipantDiffCallback) {

    class ParticipantViewHolder(itemView: View, val onClick: (View?, UserModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private lateinit var _participant: UserModel
        private val lbParticipantUsername = itemView.findViewById<TextView>(R.id.lbParticipantUsername)
        private val lbParticipantFullName = itemView.findViewById<TextView>(R.id.lbParticipantFullName)
        private val btnDeleteParticipant = itemView.findViewById<Button>(R.id.btnDeletePariticipant)

        @SuppressLint("ResourceAsColor")
        fun bind(participant: UserModel) {
            _participant = participant
            lbParticipantUsername.text = participant.username
            lbParticipantFullName.text = participant.lastname + " " + participant.firstname
            btnDeleteParticipant.setOnClickListener {
                onClick(it, participant)
            }
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
object ParticipantDiffCallback : DiffUtil.ItemCallback<UserModel>() {
    override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.username == newItem.username && oldItem.firstname == newItem.firstname && oldItem.lastname == newItem.lastname
    }
}