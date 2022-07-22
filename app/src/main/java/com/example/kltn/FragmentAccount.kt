package com.example.kltn

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kltn.services.GroupService
import com.example.kltn.services.UserService
import com.example.kltn.utils.Constants
import com.example.kltn.utils.LoginSharePrefrence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentAccount : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            val prefrence = context?.let { LoginSharePrefrence(it) }
            prefrence?.init()

            prefrence?.getUserName()?.let {
                editUserName.setText(it)
            }

            prefrence?.getPassword()?.let {
                editPassword.setText(it)
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
}