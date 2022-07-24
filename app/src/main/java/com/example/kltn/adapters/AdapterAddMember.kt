package com.example.kltn

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.models.UserModel
import com.example.kltn.utils.Roles

class AdapterAddMember(
    private val onClick: (View?, UserModel) -> Unit,
) :
    ListAdapter<UserModel, AdapterAddMember.AddMemberViewHolder>(AddMemberDiffCallback) {

    class AddMemberViewHolder(
        itemView: View,
        val onClick: (View?, UserModel) -> Unit,
    ) :
        RecyclerView.ViewHolder(itemView) {
        private val lbAddMemberUsername: TextView =
            itemView.findViewById<TextView>(R.id.lbAddMemberUsername)
        private val lbAddMemberFullName: TextView =
            itemView.findViewById<TextView>(R.id.lbAddMemberFullName)
        private val btnAddMemberDelete: Button =
            itemView.findViewById<Button>(R.id.btnAddMemberDelete)
        private lateinit var user: UserModel

        init {
            btnAddMemberDelete.setOnClickListener {
                user.let {
                    onClick(itemView, it)
                }
            }
        }

        fun bind(holder: AddMemberViewHolder, u: UserModel) {
            user = u
            lbAddMemberUsername.text = u.username
            lbAddMemberFullName.text = u.lastname + " " + u.firstname
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

object AddMemberDiffCallback : DiffUtil.ItemCallback<UserModel>() {
    override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.username == newItem.username && oldItem.firstname == newItem.firstname && oldItem.lastname == newItem.lastname
    }
}