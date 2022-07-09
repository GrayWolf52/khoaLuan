package com.example.kltn

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.models.GroupModel
import com.example.kltn.services.GroupService
import com.example.kltn.services.UserGroupService
import com.example.kltn.utils.Constants
import kotlinx.coroutines.*
import java.lang.Runnable

class FragmentGroup : Fragment() {
    private lateinit var btnAddGroup: TextView
    private lateinit var rcvGroup: RecyclerView
    private lateinit var groupAdapter: AdapterGroup
    private val listGroup = ArrayList<GroupModel>()
    private var userId: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var bundle = arguments
        if (bundle != null)
            userId = bundle!!.getInt(Constants.USER_ID)
        var view = inflater.inflate(R.layout.fragment_group, container, false)
        rcvGroup = view.findViewById(R.id.rcvGroup)
        btnAddGroup = view.findViewById(R.id.btnAddGroup)
        groupAdapter = AdapterGroup({ _, group -> onGroupClicked(group) }, {
            Log.d("TAG", "long click delete group : ")
            deleteGroup(it.id)
        })
        rcvGroup.adapter = groupAdapter
        btnAddGroup.setOnClickListener {
            try {
                var dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_addgroup)
                var btnAddGroupClose = dialog.findViewById<Button>(R.id.btnAddGroupClose)
                var btnAddGroupSave = dialog.findViewById<Button>(R.id.btnAddGroupSave)
                var txtAddGroupName = dialog.findViewById<EditText>(R.id.txtAddGroupName)
                btnAddGroupClose.setOnClickListener {
                    dialog.dismiss()
                }
                btnAddGroupSave.setOnClickListener {
                    Thread {
                        var result = GroupService.create(userId, txtAddGroupName.text.toString())
                        if (result.first.isEmpty()) {
                            activity?.runOnUiThread(Runnable {
                                Toast.makeText(
                                    requireContext(),
                                    "Tạo nhóm thành công.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                            dialog.dismiss()
                            load()
                            var intent =
                                Intent(this.requireActivity(), ActivityMember::class.java).apply {
                                    putExtra(Constants.GROUD_ID, result.second)
                                    putExtra(Constants.USER_ID, userId)
                                }
                            startActivity(intent)
                        } else {
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(), result.first, Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }
                    }.start()
                }
                var window = dialog.window
                var wlp = window?.attributes
                wlp?.gravity = Gravity.BOTTOM
                window?.attributes = wlp
                dialog.show()
                dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
            } catch (ex: Exception) {
                Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        load()
        return view
    }

    private fun onGroupClicked(group: GroupModel) {
        var intent = Intent(this.requireActivity(), ActivityMember::class.java).apply {
            putExtra(Constants.GROUD_ID, group.id)
            putExtra(Constants.USER_ID, userId)
        }
        startActivity(intent)
    }

    private fun deleteGroup(idGroup: Int) {
        Log.d("TAG", "deleteGroup: id group = $idGroup")
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("TAG", "deleteGroup: id group = $idGroup")
            GlobalScope.launch(Dispatchers.IO) {
                // do background task
                var result = GroupService.deleteGroup(idGroup)
                load()
                withContext(Dispatchers.Main) {
                    // update UI
                    Toast.makeText(context, result.first, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun load() {
        Thread {
            var result = UserGroupService.GetForUser(userId)
            if (result.first.isEmpty()) {
                if (result.second != null) {
                    activity?.runOnUiThread {
                        groupAdapter.submitList(result.second!!.filter { x -> x.group != null }
                            .map { x -> x.group })
                    }
                }
            } else {
                val runOnUiThread = activity?.runOnUiThread {
                    Toast.makeText(context, result.first, Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}