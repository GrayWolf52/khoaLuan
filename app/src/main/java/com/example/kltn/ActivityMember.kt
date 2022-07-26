package com.example.kltn

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.fragment.FragmentMember
import com.example.kltn.models.DataSuggest
import com.example.kltn.models.UserModel
import com.example.kltn.services.GroupService
import com.example.kltn.services.UserGroupService
import com.example.kltn.services.UserService
import com.example.kltn.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ActivityMember : AppCompatActivity() {

    private val selectedMember = ArrayList<UserModel>()
    private val selectedMemberAdapter =
        AdapterAddUser({ view, member -> deleteMember(view, member) })
    private lateinit var rcvMember: RecyclerView

    private var groupId: Int = 0
    private lateinit var adapterMember: AdapterMember
    private lateinit var btnMemberBack: TextView
    private lateinit var btnMemberAdd: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)
        var bundle = intent.extras
        if (bundle != null) {
            groupId = bundle.getInt(Constants.GROUD_ID)

            rcvMember = findViewById(R.id.rcvMember)
            adapterMember = AdapterMember({ view, user -> onMemberClicked(user) }, {
                it?.let {
                    removeUserGroup(it)
                }
            })
            rcvMember.adapter = adapterMember
            UserGroupService.loadDataAgain.postValue(true)
            UserGroupService.loadDataAgain.observe(this) {
                if (it) {
                    loadUserGroup()
                    UserGroupService.loadDataAgain.postValue(false)
                }
            }
        }
        btnMemberBack = findViewById(R.id.btnStaffScheduleBack)
        btnMemberBack.setOnClickListener {
            finish()
        }

        btnMemberAdd = findViewById(R.id.btnMemberAdd)
        btnMemberAdd.setOnClickListener {
            try {
                addMemberForGroup()
            } catch (ex: Exception) {
                Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteMember(view: View?, user: UserModel) {
          var newList = selectedMember.filter { !(it.id == user.id) }
          selectedMember.clear()
          for (item in newList) selectedMember.add(item)
          selectedMemberAdapter.submitList(selectedMember.toMutableList())
    }

    private fun removeUserGroup(id: Int){
        GlobalScope.launch(Dispatchers.IO) {
            val dataRepos = UserGroupService.acceptInvitation(id, groupId, false)
            withContext(Dispatchers.Main) {
              /*  Toast.makeText(this@ActivityMember, dataRepos, Toast.LENGTH_LONG).show()*/
                loadUserGroup()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG", "onResume: loading again ")
        loadUserGroup()
    }

    override fun onPause() {
        super.onPause()
        Log.d("TAG", "onPause: ")
    }

    private fun onMemberClicked(user: UserGroupInfos) {
        var intent = Intent(this, ActivityStaffSchedule::class.java).apply {
            putExtra("GroupId", groupId)
            putExtra("UserId", user.user.id)
        }
        startActivity(intent)
    }

    private fun loadUserGroup() {
        Thread {
            var result = GroupService.getMember(groupId)
            runOnUiThread {
                if (result.first.isNotEmpty())
                    Toast.makeText(this, result.first, Toast.LENGTH_SHORT).show()
                else
                    adapterMember.submitList(result.second!!.toMutableList())
            }
        }.start()
    }


    private fun addMemberForGroup() {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_addmember)
        var btnAddMemberClose = dialog.findViewById<Button>(R.id.btnAddMemberClose)
        var btnAddMemberSave = dialog.findViewById<Button>(R.id.btnAddMemberSave)
        var txtAddMemberSearch = dialog.findViewById<AutoCompleteTextView>(R.id.txtAddMemberSearch)
        var rcvAddMember = dialog.findViewById<RecyclerView>(R.id.rcvAddMember)
        rcvAddMember.adapter = selectedMemberAdapter
        val listResultMember = ArrayList<UserModel>()
        val listResultMember2 = ArrayList<UserModel>()
        val memberAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.select_dialog_item, ArrayList<String>())
        txtAddMemberSearch.setAdapter(memberAdapter)
        txtAddMemberSearch.threshold = 2
        txtAddMemberSearch.setOnItemClickListener { _, _, position, _ ->
            selectedMember.add(listResultMember2[position])
            selectedMemberAdapter.submitList(selectedMember.toMutableList())
            txtAddMemberSearch.setText("")
            txtAddMemberSearch.clearFocus()
        }
        txtAddMemberSearch.doBeforeTextChanged { _, _, _, _ ->
            listResultMember2.clear()
            for (item in listResultMember) listResultMember2.add(item)
        }
        txtAddMemberSearch.doAfterTextChanged {
            Log.d("Debug:", "doAfterTextChanged")
            if (it.toString().length >= txtAddMemberSearch.threshold) {
                Thread {
                    var selectedUsers = ArrayList<Int>()
                    for (item in selectedMember) {
                        if (!selectedUsers.contains(item.id)) {
                            selectedUsers.add(item.id)
                        }
                    }
                    var a = UserService.SearchWithoutUser(
                        it.toString(),
                        selectedUsers
                    )
                    var searchParticipants = a.second?.let {
                        it.toCollection(ArrayList<UserModel>())
                    }
                    listResultMember.clear()
                    searchParticipants?.let {
                        for (item in it) {
                            listResultMember.add(item)
                        }
                    }
                    runOnUiThread {
                        memberAdapter.clear()
                    }
                    Handler(Looper.getMainLooper()).post {
                        for (user in listResultMember) {
                            memberAdapter.add(user.username + " " + user.firstname + " " + user.lastname)
                        }
                    }
                    runOnUiThread {
                        memberAdapter.notifyDataSetChanged()
                    }
                }.start()
            }
        }
        btnAddMemberClose.setOnClickListener {
            dialog.dismiss()
        }
        btnAddMemberSave.setOnClickListener {
            Thread {
                val listId = mutableListOf<Int>()
                selectedMember.forEach {
                    listId.add(it.id)
                }
                val resultAddUser = UserGroupService.addUser(listId, groupId)
                runOnUiThread {
                    Toast.makeText(this, resultAddUser.first, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }.start()
        }
        var window = dialog.window
        var wlp = window?.attributes
        wlp?.gravity = Gravity.BOTTOM
        window?.attributes = wlp
        selectedMember.clear()
        selectedMemberAdapter.submitList(selectedMember.toMutableList())
        dialog.show()
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

}