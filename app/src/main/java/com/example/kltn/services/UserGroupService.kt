package com.example.kltn.services

import android.util.Log
import com.example.kltn.Common
import com.example.kltn.models.EventModel
import com.example.kltn.models.UserGroupModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class UserGroupService {
    companion object {
        fun GetForUser(userId: Int): Triple<String, Array<UserGroupModel>?, Int> {
            var client = OkHttpClient()
            var gson = Gson()
            val request = Request.Builder()
                .url(Common.API_HOST + "api/UserGroup/GetForUser/" + userId)
                .build()
            try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) {
                    var userGroups:Array<UserGroupModel> = gson.fromJson(responseBody, (ArrayList<UserGroupModel>()).toTypedArray().javaClass)
                    Log.d("userGroups", " userGroups size = ${userGroups.size}")
                    return Triple("",userGroups , 0)
                }
                else if (statusCode == 400) {
                    if (responseBody == null || responseBody == "") {
                        return Triple("Đã xảy ra lỗi. Vui lòng thử lại sau", null, 0)
                    }
                    return Triple(responseBody, null, 0)
                }
                else {
                    return Triple("Đã xảy ra lỗi. Vui lòng thử lại sau", null, 0)
                }
            } catch (ex: Exception) {
                return Triple("Đã xảy ra lỗi. Vui lòng thử lại sau", null, 0)
            }
        }
    }
}