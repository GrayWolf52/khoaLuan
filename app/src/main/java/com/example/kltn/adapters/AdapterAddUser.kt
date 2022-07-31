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
import com.example.kltn.models.UserModel

class AdapterAddUser(
    private val onClick: (View?, UserModel) -> Unit,
) :
    ListAdapter<UserModel, AdapterAddUser.AddUserViewHolder>(AddUserDiffCallback) {

    class AddUserViewHolder(
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

        fun bind(holder: AddUserViewHolder, u: UserModel) {
            user = u
            lbAddMemberUsername.text = u.username
            lbAddMemberFullName.text = u.firstName + " " + u.lastName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddUserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_addmember, parent, false)
        return AddUserViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: AddUserViewHolder, position: Int) {
        val u = getItem(position)
        holder.bind(holder, u)
    }
}

object AddUserDiffCallback : DiffUtil.ItemCallback<UserModel>() {
    override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.username == newItem.username && oldItem.lastName == newItem.lastName && oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.username == newItem.username && oldItem.lastName == newItem.lastName && oldItem.id == newItem.id
    }
}