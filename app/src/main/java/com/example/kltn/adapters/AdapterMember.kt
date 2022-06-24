package com.example.kltn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class AdapterMember(private val onClick: (View?, UserGroupInfos) -> Unit) :
    ListAdapter<UserGroupInfos, AdapterMember.MemberViewHolder>(MemberDiffCallback) {

    class MemberViewHolder(itemView: View, val onClick: (View?, UserGroupInfos) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val btnGroup: TextView = itemView.findViewById<Button>(R.id.btnMember)
        private lateinit var user: UserGroupInfos

        init {
            btnGroup.setOnClickListener {
                user.let {
                    onClick(itemView,it)
                }
            }
        }

        fun bind(u: UserGroupInfos) {
            user = u
            btnGroup.text = u.user.userName + " - " + u.user.lastName + " " + u.user.firstName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val u = getItem(position)
        holder.bind(u)
    }
}
object MemberDiffCallback : DiffUtil.ItemCallback<UserGroupInfos>() {
    override fun areItemsTheSame(oldItem: UserGroupInfos, newItem: UserGroupInfos): Boolean {
        return oldItem.group.id == newItem.group.id && oldItem.user.id == newItem.user.id
    }

    override fun areContentsTheSame(oldItem: UserGroupInfos, newItem: UserGroupInfos): Boolean {
        return oldItem.user.userName == newItem.user.userName && oldItem.user.firstName == newItem.user.firstName && oldItem.user.lastName == newItem.user.lastName
    }
}