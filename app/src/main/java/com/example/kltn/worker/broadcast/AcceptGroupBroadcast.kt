package com.example.kltn.worker.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.kltn.utils.Constants

class AcceptGroupBroadcast : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        when (intent?.action) {
            Constants.ACTION_NO -> {

            }

            Constants.ACTION_YES -> {
                val userId = intent.getIntExtra(Constants.USER_ID, 0)
                val groupId = intent.getIntExtra(Constants.GROUD_ID, 0)

            }
        }
    }
}