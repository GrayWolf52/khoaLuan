package com.example.kltn.worker.broadcast

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.kltn.services.UserGroupService
import com.example.kltn.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AcceptGroupBroadcast : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        Log.d("TAG", "onReceive: ${intent?.action} ")
        when (intent?.action) {
            Constants.ACTION_NO -> {
                val userId = intent.getIntExtra(Constants.USER_ID, 0)
                val groupId = intent.getIntExtra(Constants.GROUD_ID, 0)
                val notification = intent.getIntExtra(Constants.NOTIFICATION_ID, -1)
                (p0?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancel(
                    notification
                )
                Log.d("TAG", "onReceive: $userId && $groupId")

            }

            Constants.ACTION_YES -> {
                val userId = intent.getIntExtra(Constants.USER_ID, 0)
                val groupId = intent.getIntExtra(Constants.GROUD_ID, 0)
                val notification = intent.getIntExtra(Constants.NOTIFICATION_ID, -1)
                (p0?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancel(
                    notification
                )
                GlobalScope.launch(Dispatchers.IO) {
                    val dataRepos = UserGroupService.acceptInvitation(userId, groupId)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(p0, dataRepos, Toast.LENGTH_LONG).show()
                    }
                }
                Log.d("TAG", "onReceive: $userId && $groupId")
            }
        }
    }
}