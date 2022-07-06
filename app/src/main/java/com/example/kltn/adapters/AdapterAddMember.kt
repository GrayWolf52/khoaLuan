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
    private val onClickSpiner: (Int, UserModel) -> Unit
) :
    ListAdapter<UserModel, AdapterAddMember.AddMemberViewHolder>(AddMemberDiffCallback) {

    class AddMemberViewHolder(
        itemView: View,
        val onClick: (View?, UserModel) -> Unit,
        val onClickSpiner: (Int, UserModel) -> Unit
    ) :
        RecyclerView.ViewHolder(itemView) {
        private val lbAddMemberUsername: TextView =
            itemView.findViewById<TextView>(R.id.lbAddMemberUsername)
        private val lbAddMemberFullName: TextView =
            itemView.findViewById<TextView>(R.id.lbAddMemberFullName)
        private val btnAddMemberDelete: Button =
            itemView.findViewById<Button>(R.id.btnAddMemberDelete)
        private val spAddMemberPosition: Spinner =
            itemView.findViewById<Spinner>(R.id.spAddMemberPosition)
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
            spAddMemberPosition.setSelection(u.roles)
            var roles = ArrayList<String>()
            roles.add(Roles.TRUONG_NHOM)
            roles.add(Roles.THANH_VIEN)
            roles.add(Roles.TRO_LY)
            spAddMemberPosition.adapter = ArrayAdapter<String>(
                holder.itemView.context,
                R.layout.support_simple_spinner_dropdown_item,
                roles
            )
            spAddMemberPosition.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        when (roles[p2]) {
                            Roles.THANH_VIEN -> user.roles = 2
                            Roles.TRO_LY -> user.roles = 3
                            Roles.TRUONG_NHOM -> user.roles = 1
                        }
                        onClickSpiner(position, user)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        Log.d("AdapterAddMember", "onNothingSelected")
                    }

                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddMemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_addmember, parent, false)
        return AddMemberViewHolder(view, onClick, onClickSpiner)
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