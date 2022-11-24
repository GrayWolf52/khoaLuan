package com.example.kltn

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.security.identity.AccessControlProfile
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kltn.services.GroupService
import com.example.kltn.services.UserGroupService
import com.example.kltn.services.UserService
import com.example.kltn.utils.Constants
import com.example.kltn.utils.LoginSharePrefrence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class FragmentAccount : Fragment() {
    private lateinit var txtUserNameProfile: TextView
    private lateinit var txtNameProfile: TextView
    private lateinit var txtPhoneProfile: TextView
    private lateinit var txtAddressProfile: TextView
    private var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var bundle = arguments
        if (bundle != null)
            userId = bundle!!.getInt(Constants.USER_ID)
        var view = inflater.inflate(R.layout.fragment_account, container, false)
        var btnLogout = view.findViewById<Button>(R.id.btnLogout)
        var btnResetPassword = view.findViewById<Button>(R.id.btnResetPassword)
        btnLogout.setOnClickListener {
            val intent = Intent(context, ActivityLogin::class.java)
            startActivity(intent)
        }
        btnResetPassword.setOnClickListener {
            resetPassword()
        }
        txtUserNameProfile = view.findViewById(R.id.txtUsernameProfile)
        txtNameProfile = view.findViewById(R.id.txtNameProfile)
        txtPhoneProfile = view.findViewById(R.id.txtPhoneProfile)
        txtAddressProfile = view.findViewById(R.id.txtAddressProfile)
        loadProfile()
        return view
    }

    private fun resetPassword() {
        try {
            var dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_reset_password)
            var btnSave = dialog.findViewById<Button>(R.id.btnLogin)
            var btnClose = dialog.findViewById<Button>(R.id.btnClose)
            var editUserName = dialog.findViewById<EditText>(R.id.txtUsername)
            var editPassword = dialog.findViewById<EditText>(R.id.txtPassword)
            var editResetPassword = dialog.findViewById<EditText>(R.id.edit_reset_password)
            var txtError = dialog.findViewById<TextView>(R.id.lbLoginMsg)
            val prefrence = context?.let { LoginSharePrefrence(it) }
            prefrence?.init()

            prefrence?.getUserName()?.let {
                editUserName.setText(it)
            }
            btnClose.setOnClickListener {
                dialog.dismiss()
            }
            btnSave.setOnClickListener {
                Log.d("TAG", "resetPassword: ")
                GlobalScope.launch(Dispatchers.IO) {
                    Log.d("TAG", "resetPassword:12233 ")
                    val userName = editUserName.text.toString()
                    val oldPassword = editPassword.text.toString()
                    val newPassword = editResetPassword.text.toString()
                    val password = prefrence?.getPassword()
                    if (password != oldPassword) {
                        txtError.setText( "Mật khẩu cũ của bạn không đúng!")
                    }else {
                        if (oldPassword == newPassword) {
                            txtError.setText("Mật khẩu mới trùng với mật khẩu cũ!")
                        } else {
                            var result = UserService.resetPassword(userName, oldPassword, newPassword)
                            withContext(Dispatchers.Main) {
                                if (result.equals("Đổi mật khẩu thành công")) {
                                    Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                                    prefrence?.setPassword(newPassword)
                                    dialog.dismiss()
                                } else {
                                    Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }
            var window = dialog.window
            var wlp = window?.attributes
            wlp?.gravity = Gravity.BOTTOM
            window?.attributes = wlp
            dialog.show()
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        } catch (ex: Exception) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    private fun loadProfile() {
        Thread {
            var result = UserService.getUser(userId)
            if (result.first.isEmpty()) {
                if (result.second != null) {
                    activity?.runOnUiThread {
                        var descrip = result.second
                        descrip?.username?.let {
                            txtUserNameProfile.setText(it)
                        }
                        txtNameProfile.setText(descrip?.firstName + " " + descrip?.lastName)

                        descrip?.phone?.let {
                            txtPhoneProfile.setText(it)
                        }
                        descrip?.address?.let {
                            txtAddressProfile.setText(it)
                        }
                    }
                }
            }
            else {
                val runOnUiThread = activity?.runOnUiThread {
                    Toast.makeText(context, result.first, Toast.LENGTH_SHORT).show()
                }
            }

        }.start()

    }
}