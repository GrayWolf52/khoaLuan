package com.example.kltn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.models.GroupModel

class AdapterGroup(
    private val onClick: (View?, GroupModel) -> Unit,
    private val onClickDelete: (GroupModel) -> Unit
) :
    ListAdapter<GroupModel, AdapterGroup.GroupViewHolder>(GroupDiffCallback) {

    class GroupViewHolder(itemView: View, val onClick: (View?, GroupModel) -> Unit, val onClickDelete: ( GroupModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val btnGroup: TextView = itemView.findViewById<Button>(R.id.btnGroup)
        private val selectionGroup: ImageButton = itemView.findViewById(R.id.selectionGroup)
        private lateinit var group: GroupModel

        init {
            btnGroup.setOnClickListener {
                onClick(itemView, group)
            }

          /*  btnGroup.setOnLongClickListener {
                onClickDelete(group)
                true
            }*/
            selectionGroup.setOnClickListener {
                onClickDelete(group)
            }
        }

        fun bind(g: GroupModel) {
            group = g
            btnGroup.text = g.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view, onClick, onClickDelete)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = getItem(position)
        holder.bind(group)
    }
}

object GroupDiffCallback : DiffUtil.ItemCallback<GroupModel>() {
    override fun areItemsTheSame(oldItem: GroupModel, newItem: GroupModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GroupModel, newItem: GroupModel): Boolean {
        return oldItem.name == newItem.name
    }
}