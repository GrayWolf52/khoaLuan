package com.example.kltn

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.models.GroupModel
import com.example.kltn.services.GroupService

class FragmentGroup: Fragment() {
    private lateinit var btnAddGroup: TextView
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
        btnAddGroup = view.findViewById(R.id.btnAddGroup)
        groupAdapter = AdapterGroup { view, group -> onGroupClicked(group) }
        rcvGroup.adapter = groupAdapter
        listGroup.clear()
        for (group in GroupService.get())
            listGroup.add(group)
        groupAdapter.submitList(listGroup)
        btnAddGroup.setOnClickListener {
            try {
                var dialog = Dialog(requireContext(), R.style.DialogTheme)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_addgroup)
                var btnAddGroupClose = dialog.findViewById<Button>(R.id.btnAddGroupClose)
                var btnAddGroupSave = dialog.findViewById<Button>(R.id.btnAddGroupSave)
                var txtAddGroupName = dialog.findViewById<EditText>(R.id.txtAddGroupName)
                btnAddGroupClose.setOnClickListener {
                    dialog.dismiss()
                }
                btnAddGroupSave.setOnClickListener {
                    var result = GroupService.create(txtAddGroupName.text.toString())
                    if (result) {
                        Toast.makeText(requireContext(), "Tạo nhóm thành công.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    else {
                        Toast.makeText(requireContext(), "Tạo nhóm không thành công. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show()
                    }
                }
                var window = dialog.window
                var wlp = window?.attributes
                wlp?.gravity = Gravity.BOTTOM
                window?.attributes = wlp
                dialog.show()
                dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            }
            catch (ex: Exception) {
                Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    fun onGroupClicked(group: GroupModel) {
        var intent = Intent(this.requireActivity(), ActivityMember::class.java).apply {
            putExtra("GroupId", group.id)
        }
        startActivity(intent)
    }
}