package com.example.kltn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class ActivityMain : AppCompatActivity() {
    private lateinit var fragmentHome: FragmentHome
    private lateinit var fragmentSchedule: FragmentSchedule
    private lateinit var fragmentAccount: FragmentAccount
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentHome = FragmentHome()
        fragmentSchedule = FragmentSchedule()
        fragmentAccount = FragmentAccount()
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragmentHome, "Home").commit()
        var navbar = findViewById<BottomNavigationView>(R.id.mainNavBar)
        navbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_1  -> {
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragmentHome, "Home").commit();
                }
                R.id.nav_2 -> {
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragmentSchedule, "Schedule").commit();
                }
                R.id.nav_3 -> {
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragmentAccount, "Account").commit();
                }
            }
            true
        }
    }

}