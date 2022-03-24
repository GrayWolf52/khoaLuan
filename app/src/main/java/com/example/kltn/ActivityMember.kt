package com.example.kltn

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.models.UserModel
import com.example.kltn.services.GroupService

class ActivityMember  : AppCompatActivity () {
    private lateinit var recylerViewMember: RecyclerView
    private lateinit var btnMemberBack: TextView
    private lateinit var adapterMember: AdapterMember
    private var groupId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)
        var bundle = intent.extras
        if (bundle != null)
            groupId = bundle!!.getInt("GroupId")
        recylerViewMember = findViewById(R.id.recyclerViewMember)
        btnMemberBack = findViewById(R.id.btnStaffScheduleBack)
        btnMemberBack.setOnClickListener {
            finish()
        }
        adapterMember = AdapterMember{ view, user -> onMemberClicked(user) }
        recylerViewMember.adapter = adapterMember
        adapterMember.submitList(GroupService.getMember(groupId))
    }

    fun onMemberClicked(user: UserModel) {
        var intent = Intent(this, ActivityStaffSchedule::class.java).apply {
            putExtra("GroupId", groupId)
            putExtra("UserId", user.id)
        }
        startActivity(intent)
    }
}