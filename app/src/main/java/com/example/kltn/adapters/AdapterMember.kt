package com.example.kltn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.models.GroupModel
import com.example.kltn.models.UserModel

class AdapterMember(private val onClick: (View?, UserModel) -> Unit) :
    ListAdapter<UserModel, AdapterMember.MemberViewHolder>(MemberDiffCallback) {

    class MemberViewHolder(itemView: View, val onClick: (View?, UserModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val btnGroup: TextView = itemView.findViewById<Button>(R.id.btnMember)
        private lateinit var user: UserModel

        init {
            btnGroup.setOnClickListener {
                user.let {
                    onClick(itemView, it)
                }
            }
        }

        fun bind(u: UserModel) {
            user = u
            btnGroup.text = u.username + " - " + u.lastname + " " + u.firstname
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
object MemberDiffCallback : DiffUtil.ItemCallback<UserModel>() {
    override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.username == newItem.username && oldItem.firstname == newItem.firstname && oldItem.lastname == newItem.lastname
    }
}