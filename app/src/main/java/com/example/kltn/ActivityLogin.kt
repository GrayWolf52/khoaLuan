package com.example.kltn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kltn.utils.Constants
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray

class ActivityLogin : AppCompatActivity() {
    lateinit var btnLogin: Button;
    lateinit var txtUsername: EditText
    lateinit var txtPassword: EditText
    lateinit var lbLoginMsg: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener(){
            lbLoginMsg.text = ""
            Thread({
                login()
            }).start()
        }
        txtUsername = findViewById(R.id.txtUsername)
        txtPassword = findViewById(R.id.txtPassword)
        lbLoginMsg = findViewById(R.id.lbLoginMsg)
    }

    private fun login() {
        var Json = MediaType.parse("application/json; charset=utf-8")
        var gson = Gson()
        var a = LoginInfos()
        a.Username = txtUsername.text.toString()
        a.Password = txtPassword.text.toString()
        var requestBody = RequestBody.create(Json, gson.toJson(a))
        var client = OkHttpClient()
        val request = Request.Builder()
            .url(Common.API_HOST + "api/User/Login")
            .post(requestBody)
            .build()
        try {
            var response = client.newCall(request).execute()
            var statusCode = response.code()
            var responseBody = response.body()?.string()
            if (statusCode == 200) {
                val intent = Intent(this, ActivityMain::class.java).apply {
                    putExtra(Constants.USER_ID, responseBody!!.toInt());
                }
                startActivity(intent)
            }
            else if (statusCode == 400) {
                runOnUiThread(Runnable {
                    lbLoginMsg.text = responseBody
                })
            }
            else {
                runOnUiThread(Runnable {
                    lbLoginMsg.text = "Đã xảy ra lỗi trong quá trình đăng nhập. Vui lòng thử lại sau."
                })
            }
        } catch (ex: Exception) {
            runOnUiThread(Runnable {
                lbLoginMsg.text = "Đã xảy ra lỗi trong quá trình đăng nhập. Vui lòng thử lại sau."
            })
        }
    }
    private fun updateUI(onLoad: Boolean) {
        txtUsername.isEnabled = !onLoad
        txtPassword.isEnabled = !onLoad
        btnLogin.isEnabled = !onLoad
    }
}
