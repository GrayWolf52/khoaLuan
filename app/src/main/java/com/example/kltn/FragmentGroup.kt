package com.example.kltn

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.models.GroupModel
import com.example.kltn.services.GroupService

class FragmentGroup: Fragment() {
    private lateinit var rcvGroup: RecyclerView
    private lateinit var groupAdapter: AdapterGroup
    private val listGroup = ArrayList<GroupModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_group, container, false)
        rcvGroup = view.findViewById(R.id.rcvGroup)
        groupAdapter = AdapterGroup { view, group -> onGroupClicked(group) }
        rcvGroup.adapter = groupAdapter
        listGroup.clear()
        for (group in GroupService.get())
            listGroup.add(group)
        groupAdapter.submitList(listGroup)
        return view
    }

    fun onGroupClicked(group: GroupModel) {
        var intent = Intent(this.requireActivity(), ActivityMember::class.java).apply {
            putExtra("GroupId", group.id)
        }
        startActivity(intent)
    }
}