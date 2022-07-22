package com.example.kltn.utils

import android.content.Context
import android.content.SharedPreferences

class LoginSharePrefrence(private val context: Context) {

    private var sharedPrefrence: SharedPreferences? = null

    private var editor: SharedPreferences.Editor? = null

    fun init() {
        sharedPrefrence = context.getSharedPreferences(PREFRENCE, Context.MODE_PRIVATE)
        editor = sharedPrefrence?.edit()
    }

    fun setUserName(userName: String) {
        editor?.putString(USER_NAME, userName)
        editor?.commit()
    }

    fun getUserName(): String? = sharedPrefrence?.getString(USER_NAME, null)

    fun setPassword(pass: String) {
        editor?.putString(PASSWORD, pass)
        editor?.commit()
    }

    fun getPassword(): String? = sharedPrefrence?.getString(PASSWORD, null)

    companion object {
        const val PREFRENCE = "MyFrefrence"
        const val USER_NAME = "UserName"
        const val PASSWORD = "Password"
    }
}