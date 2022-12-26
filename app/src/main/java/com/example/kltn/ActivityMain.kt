package com.example.kltn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.kltn.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView

class ActivityMain : AppCompatActivity() {
    private lateinit var fragmentCalendar: FragmentCalender
    private lateinit var fragmentHome: HomeFragment
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
        fragmentHome = HomeFragment()
        fragmentHome.arguments = bundle
        fragmentCalendar = FragmentCalender()
        fragmentCalendar.arguments = bundle
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
                        .replace(R.id.fragmentContainer, fragmentCalendar, "Calendar").commit();
                }
                R.id.nav_3 -> {
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragmentGroup, "Schedule").commit();
                }
                R.id.nav_4 -> {
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragmentAccount, "Account").commit();
                }
            }
            true
        }
      //  onWorkerInvitation()
    }

}