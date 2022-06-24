package com.example.kltn.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.kltn.ActivityStaffSchedule
import com.example.kltn.AdapterMember
import com.example.kltn.R
import com.example.kltn.UserGroupInfos
import com.example.kltn.models.UserModel
import com.example.kltn.services.GroupService
import com.example.kltn.services.UserService

class FragmentMember : Fragment() {
    private lateinit var rcvMember: RecyclerView

    private var groupId: Int = 0
    private lateinit var adapterMember: AdapterMember
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_member, container, false);
        groupId = this.requireArguments().getInt("groupId")
        rcvMember = view.findViewById(R.id.rcvMember)
        adapterMember = AdapterMember{ view, user -> onMemberClicked(user) }
        rcvMember.adapter = adapterMember
        Thread({
            var result = GroupService.getMember(groupId)
            activity?.runOnUiThread({
                if (result.first.length > 0)
                    Toast.makeText(context, result.first, Toast.LENGTH_SHORT).show()
                else
                    adapterMember.submitList(result.second!!.toMutableList())
            })
        }).start()
        return view
    }
    fun onMemberClicked(user: UserGroupInfos) {
        var intent = Intent(this.context, ActivityStaffSchedule::class.java).apply {
            putExtra("GroupId", groupId)
            putExtra("UserId", user.user.id)
        }
        startActivity(intent)
    }
}