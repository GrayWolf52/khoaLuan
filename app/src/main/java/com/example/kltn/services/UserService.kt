package com.example.kltn.services

import android.util.Log
import com.example.kltn.Common
import com.example.kltn.GroupInfos
import com.example.kltn.models.EventModel
import com.example.kltn.models.UserModel
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.net.URLEncoder
import java.util.ArrayList

class UserService {
    companion object {
        fun SearchWithout(prefix: String, exclusions: List<Int>): Triple<String, Array<UserModel>?, Int> {
            Log.d("SearchWithout", "SearchWithout")
            var gson = Gson()
            var client = OkHttpClient()
            val request = Request.Builder()
                .url(Common.API_HOST + "api/User/Search?prefix=" + URLEncoder.encode(prefix, "utf-8") + "&exclusions=" + URLEncoder.encode(exclusions.joinToString(",")))
                .build()
            try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) {
                    var users:Array<UserModel> = gson.fromJson(responseBody, (ArrayList<UserModel>()).toTypedArray().javaClass)
                    Log.d("SearchWithout", "SearchWithout users = ${users.size}")
                    return Triple("",users , 0)
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
