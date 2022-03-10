package com.example.kltn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ActivityLogin : AppCompatActivity() {

    lateinit var btnLogin: Button;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener(){
            login()
        }
    }

    private fun login() {
        //Lấy username vs password từ màn hình
        //Gọi Api Login
        //Trong response chứa UserId
        val intent = Intent(this, ActivityMain::class.java).apply {
            putExtra("UserId", 1);
        }
        startActivity(intent)
    }
}
