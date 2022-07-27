package com.example.kltn

import android.app.AlertDialog
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
import com.example.kltn.utils.LoginSharePrefrence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
            setDialogOption(it.id, it.name)
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
                val txtError = dialog.findViewById<TextView>(R.id.txtError)
                btnAddGroupClose.setOnClickListener {
                    dialog.dismiss()
                }
                btnAddGroupSave.setOnClickListener {
                    Thread {
                        if (txtAddGroupName.text.isEmpty() || txtAddGroupName.text.length < 5) {
                            activity?.runOnUiThread {
                                txtError.setText(
                                    "Vui lòng nhập tên cho nhóm. Tên nhóm có độ dài từ 5 kí tự trở lên."
                                )
                            }
                        } else {
                            var result =
                                GroupService.create(userId, txtAddGroupName.text.toString())
                            if (result.first.isEmpty()) {
                                activity?.runOnUiThread {
                                    Toast.makeText(
                                        requireContext(),
                                        "Tạo nhóm thành công.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                dialog.dismiss()
                                load()
                                var intent =
                                    Intent(
                                        this.requireActivity(),
                                        ActivityMember::class.java
                                    ).apply {
                                        putExtra(Constants.GROUD_ID, result.second)
                                        putExtra(Constants.USER_ID, userId)
                                    }
                                startActivity(intent)
                            } else {
                                activity?.runOnUiThread {
                                    Toast.makeText(
                                        requireContext(),
                                        result.first,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }

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


    private fun setDialogOption(idGroup: Int, nameGroup: String) {
        activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Tính năng group")
                .setMessage("Bạn có xóa hay đổi tên nhóm?")
                .setPositiveButton("Xóa") { dialog, _ ->
                    deleteGroup(idGroup)
                    dialog.cancel()
                }.setNegativeButton("Đổi tên") { dialog, _ ->
                    renameGroup(idGroup, nameGroup)
                    dialog.cancel()
                }
            builder.create()
            builder.show()
        }
    }


    private fun renameGroup(idGroup: Int, nameGroup: String) {
        try {
            var dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_rename_group)
            var btnRenameGroup = dialog.findViewById<Button>(R.id.btnAddGroupSave)
            var btnClose = dialog.findViewById<Button>(R.id.btnAddGroupClose)
            var txtOldName = dialog.findViewById<EditText>(R.id.edtNameOld)
            var txtNewName = dialog.findViewById<EditText>(R.id.edtRenameGroup)
            val txtError = dialog.findViewById<TextView>(R.id.txtError)
            txtOldName.setText(nameGroup)
            btnClose.setOnClickListener {
                dialog.dismiss()
            }
            btnRenameGroup.setOnClickListener {
                Thread {
                    if (txtOldName.text.isEmpty() || txtNewName.text.length < 5) {
                        activity?.runOnUiThread {
                            txtError.setText(
                                "Vui lòng nhập tên mới cho nhóm. Tên nhóm có độ dài từ 5 kí tự trở lên."
                            )
                        }
                    } else {
                        var result =
                            GroupService.changeNameGroup(idGroup, txtNewName.text.toString())
                        if (result.first == "Đã đổi tên nhóm thành công") {
                            load()
                            dialog.dismiss()
                        }
                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                result.first,
                                Toast.LENGTH_SHORT
                            )
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

    private fun deleteGroup(idGroup: Int) {
        Log.d("TAG", "deleteGroup: id group = $idGroup")
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("TAG", "deleteGroup: id group = $idGroup")
            activity?.let {
                Log.d("TAG", "deleteGroup1111: id group = $idGroup")
                val builder = AlertDialog.Builder(it)
                builder.setTitle("Xóa group")
                    .setMessage("Bạn có chắc chắn muốn xóa group này không?")
                    .setPositiveButton("Có") { _, _ ->
                        GlobalScope.launch(Dispatchers.IO) {
                            // do background task
                            var result = GroupService.deleteGroup(idGroup)
                            load()
                            withContext(Dispatchers.Main) {
                                // update UI
                                load()
                                Toast.makeText(context, result.first, Toast.LENGTH_LONG).show()
                            }
                        }
                    }.setNegativeButton("Không") { dialog, _ ->
                        dialog.cancel()
                    }
                builder.create()
                builder.show()
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