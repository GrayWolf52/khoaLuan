package com.example.kltn

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.work.*
import com.example.kltn.utils.Constants
import com.example.kltn.worker.NotificationWorker
import com.example.kltn.worker.notification.AcceptGroupNotification
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit

class ActivityMain : AppCompatActivity() {
    private lateinit var fragmentHome: FragmentHome
    private lateinit var fragmentGroup: FragmentGroup
    private lateinit var fragmentAccount: FragmentAccount
    private var userId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        var bundle = intent.extras
        if (bundle != null)
            userId = bundle!!.getInt(Constants.USER_ID)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentHome = FragmentHome()
        fragmentHome.arguments = bundle
        fragmentGroup = FragmentGroup()
        fragmentGroup.arguments = bundle
        fragmentAccount = FragmentAccount()
        fragmentAccount.arguments = bundle
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragmentHome, "Home")
            .commit()
        var navbar = findViewById<BottomNavigationView>(R.id.mainNavBar)
        navbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_1 -> {
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragmentHome, "Home").commit();
                }
                R.id.nav_2 -> {
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragmentGroup, "Schedule").commit();
                }
                R.id.nav_3 -> {
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragmentAccount, "Account").commit();
                }
            }
            true
        }
        onWorkerInvitation()
    }


    private fun onWorkerInvitation() {
        Log.d("TAG", "onWorkerInvitation: ")
        val workerRequest: PeriodicWorkRequest =
            PeriodicWorkRequest.Builder(NotificationWorker::class.java, 1, TimeUnit.MINUTES)
                .setInputData(workDataOf(Constants.USER_ID to userId)).build()
// kiểu nó c
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "sendLog", ExistingPeriodicWorkPolicy.REPLACE, workerRequest)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workerRequest.id).observe(this) {
            Log.d("TAG", "onWorkerInvitation:111 ${it.state}")

                Log.d("TAG", "onWorkerInvitation: ")
                val idUser = it.outputData.getInt(NotificationWorker.ID_USER, 0)
                val idGroup = it.outputData.getInt(NotificationWorker.ID_GROUP, 0)
                val name = it.outputData.getString(NotificationWorker.INVITATION)
                Log.d("TAG", "onWorkerInvitation: $userId $idGroup $name")
                AcceptGroupNotification(context = this).acceptRequest(idUser, idGroup, name)

        }
    }
}