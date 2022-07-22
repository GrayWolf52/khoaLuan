package com.example.kltn.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
import com.example.kltn.services.UserGroupService
import com.example.kltn.services.UserService
import com.example.kltn.utils.Constants
import com.example.kltn.utils.Constants.GROUD_ID

class FragmentMember : AppCompatActivity() {
    private lateinit var rcvMember: RecyclerView

    private var groupId: Int = 0
    private lateinit var adapterMember: AdapterMember

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_member)
        var bundle = intent.extras
        if (bundle != null) {
            groupId = bundle.getInt(GROUD_ID)

            rcvMember = findViewById(R.id.rcvMember)
            adapterMember = AdapterMember { view, user -> onMemberClicked(user) }
            rcvMember.adapter = adapterMember
            UserGroupService.loadDataAgain.postValue(true)
            UserGroupService.loadDataAgain.observe(this){
                if (it){
                    loadUserGroup()
                    UserGroupService.loadDataAgain.postValue(false)
                }
            }
        }
    }
    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_member, container, false);
        arguments?.let {
            groupId = it.getInt(GROUD_ID)
        }
        rcvMember = view.findViewById(R.id.rcvMember)
        adapterMember = AdapterMember { view, user -> onMemberClicked(user) }
        rcvMember.adapter = adapterMember
        UserGroupService.loadDataAgain.postValue(true)
        UserGroupService.loadDataAgain.observe(viewLifecycleOwner){
            if (it){
                loadUserGroup()
                UserGroupService.loadDataAgain.postValue(false)
            }
        }
        return view
    }
*/
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
}