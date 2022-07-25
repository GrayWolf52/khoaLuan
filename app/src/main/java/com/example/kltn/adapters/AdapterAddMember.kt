package com.example.kltn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.models.DataSuggest

class AdapterAddMember(
    private val onClick: (View?, DataSuggest) -> Unit,
) :
    ListAdapter<DataSuggest, AdapterAddMember.AddMemberViewHolder>(AddMemberDiffCallback) {

    class AddMemberViewHolder(
        itemView: View,
        val onClick: (View?, DataSuggest) -> Unit,
    ) :
        RecyclerView.ViewHolder(itemView) {
        private val lbAddMemberUsername: TextView =
            itemView.findViewById<TextView>(R.id.lbAddMemberUsername)
        private val lbAddMemberFullName: TextView =
            itemView.findViewById<TextView>(R.id.lbAddMemberFullName)
        private val btnAddMemberDelete: Button =
            itemView.findViewById<Button>(R.id.btnAddMemberDelete)
        private lateinit var user: DataSuggest

        init {
            btnAddMemberDelete.setOnClickListener {
                user.let {
                    onClick(itemView, it)
                }
            }
        }

        fun bind(holder: AddMemberViewHolder, u: DataSuggest) {
            user = u
            lbAddMemberUsername.text = u.shortname
            lbAddMemberFullName.text = u.fullname
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddMemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_addmember, parent, false)
        return AddMemberViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: AddMemberViewHolder, position: Int) {
        val u = getItem(position)
        holder.bind(holder, u)
    }
}

object AddMemberDiffCallback : DiffUtil.ItemCallback<DataSuggest>() {
    override fun areItemsTheSame(oldItem: DataSuggest, newItem: DataSuggest): Boolean {
        return oldItem.shortname == newItem.shortname && oldItem.fullname == newItem.fullname && oldItem.type == newItem.type
    }

    override fun areContentsTheSame(oldItem: DataSuggest, newItem: DataSuggest): Boolean {
        return oldItem.shortname == newItem.shortname && oldItem.fullname == newItem.fullname && oldItem.type == newItem.type
    }
}