package com.example.kltn

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
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
import com.example.kltn.models.UserModel
import com.example.kltn.services.GroupService
import com.example.kltn.services.UserService
import com.google.android.material.bottomnavigation.BottomNavigationView

class ActivityMember  : AppCompatActivity () {
    private lateinit var btnMemberBack: TextView
    private lateinit var btnMemberAdd: TextView
    private val selectedMember = ArrayList<UserModel>()
    private val selectedMemberAdapter = AdapterAddMember { view, member -> deleteMember(view, member)}
    private lateinit var fragmentMember: FragmentMember
    private lateinit var fragmentSchedule: FragmentSchedule

    private var groupId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)
        var bundle = intent.extras
        if (bundle != null)
            groupId = bundle!!.getInt("GroupId")
        btnMemberBack = findViewById(R.id.btnStaffScheduleBack)
        btnMemberBack.setOnClickListener {
            finish()
        }
        btnMemberAdd = findViewById(R.id.btnMemberAdd)
        btnMemberAdd.setOnClickListener {
            try {
                var dialog = Dialog(this, R.style.DialogTheme)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_addmember)
                var btnAddMemberClose = dialog.findViewById<Button>(R.id.btnAddMemberClose)
                var btnAddMemberSave = dialog.findViewById<Button>(R.id.btnAddMemberSave)
                var txtAddMemberSearch = dialog.findViewById<AutoCompleteTextView>(R.id.txtAddMemberSearch)
                var rcvAddMember = dialog.findViewById<RecyclerView>(R.id.rcvAddMember)
                rcvAddMember.adapter = selectedMemberAdapter
                val listResultMember = ArrayList<UserModel>()
                val listResultMember2 = ArrayList<UserModel>()
                val memberAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, ArrayList<String>())
                txtAddMemberSearch.setAdapter(memberAdapter)
                txtAddMemberSearch.threshold = 2
                txtAddMemberSearch.setOnItemClickListener { parent, view, position, id ->
                    selectedMember.add(listResultMember2[position])
                    selectedMemberAdapter.submitList(selectedMember.toMutableList())
                    txtAddMemberSearch.setText("")
                    txtAddMemberSearch.clearFocus()
                }
                txtAddMemberSearch.doBeforeTextChanged { text, start, count, after ->
                    listResultMember2.clear()
                    for (item in listResultMember) listResultMember2.add(item)
                }
                txtAddMemberSearch.doAfterTextChanged {
                    Log.d("Debug:", "doAfterTextChanged")
                    if (it.toString().length >= txtAddMemberSearch.threshold) {
                        var selectedUsers = ArrayList<Int>()
                        for (item in selectedMember) {
                            if (selectedUsers.contains(item.id)) continue
                            selectedUsers.add(item.id)
                        }
                        var searchParticipants = UserService.SearchWithout(
                            it.toString(),
                            selectedUsers
                        ) as ArrayList<UserModel>
                        listResultMember.clear()
                        for (item in searchParticipants) listResultMember.add(item)
                        memberAdapter.clear()
                        for (user in listResultMember) {
                            memberAdapter.add(user.username + " - " + user.lastname + " " + user.firstname)
                        }

                        memberAdapter.notifyDataSetChanged()
                    }
                }
                btnAddMemberClose.setOnClickListener {
                    dialog.dismiss()
                }
                btnAddMemberSave.setOnClickListener {
                    var result = true
                    if (result) {
                        Toast.makeText(this, "Gửi lời mời tham gia nhóm thành công.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    else {
                        Toast.makeText(this, "Gửi lời mời tham gia nhóm không thành công. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show()
                    }
                }
                var window = dialog.window
                var wlp = window?.attributes
                wlp?.gravity = Gravity.BOTTOM
                window?.attributes = wlp
                selectedMember.clear()
                selectedMemberAdapter.submitList(selectedMember.toMutableList())
                dialog.show()
                dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            }
            catch (ex: Exception) {
                Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentMember = FragmentMember()
        fragmentSchedule = FragmentSchedule()
        var fragmentBundle = Bundle()
        fragmentBundle.putInt("groupId", groupId)
        fragmentMember.arguments = fragmentBundle
        fragmentSchedule.arguments = fragmentBundle

        fragmentManager.beginTransaction().add(R.id.fragmentGroupContainer, fragmentSchedule, "Lịch").commit()
        var navbar = findViewById<BottomNavigationView>(R.id.groupNavBar)
        navbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_4  -> {
                    fragmentManager.beginTransaction().replace(R.id.fragmentGroupContainer,  fragmentSchedule, "Lịch").commit();
                }
                R.id.nav_5 -> {
                    fragmentManager.beginTransaction().replace(R.id.fragmentGroupContainer, fragmentMember, "Thành viên").commit();
                }
            }
            true
        }
    }

    fun onMemberClicked(user: UserModel) {
        var intent = Intent(this, ActivityStaffSchedule::class.java).apply {
            putExtra("GroupId", groupId)
            putExtra("UserId", user.id)
        }
        startActivity(intent)
    }

    fun deleteMember(view: View?, user: UserModel) {
        var newList = selectedMember.filter { !(it.id == user.id) }
        selectedMember.clear()
        for (item in newList) selectedMember.add(item)
        selectedMemberAdapter.submitList(selectedMember.toMutableList())
    }
}