package com.example.kltn.services

import android.util.Log
import com.example.kltn.Common
import com.example.kltn.GroupInfos
import com.example.kltn.models.DataSuggest
import com.example.kltn.models.EventModel
import com.example.kltn.models.GroupModel
import com.example.kltn.models.UserModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.net.URLEncoder
import java.util.ArrayList

class UserService {
    companion object {
        fun SearchWithout(
            prefix: String,
            exclusions: List<Int>
        ): Triple<String, Array<DataSuggest>?, Int> {
            Log.d("SearchWithout", "SearchWithout")
            var gson = Gson()
            var client = OkHttpClient()
            val request = Request.Builder()
                .url(
                    Common.API_HOST + "api/UserGroup/Search?prefix=" + URLEncoder.encode(
                        prefix,
                        "utf-8"
                    ) + "&exclusions=" + URLEncoder.encode(exclusions.joinToString(","))
                )
                .build()
            try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) {
                    var users: Array<DataSuggest> = gson.fromJson(
                        responseBody,
                        (ArrayList<DataSuggest>()).toTypedArray().javaClass
                    )
                    Log.d("SearchWithout", "SearchWithout users = ${users.size}")
                    return Triple("", users, 0)
                } else if (statusCode == 400) {
                    if (responseBody == null || responseBody == "") {
                        return Triple("Đã xảy ra lỗi. Vui lòng thử lại sau", null, 0)
                    }
                    return Triple(responseBody, null, 0)
                } else {
                    return Triple("Đã xảy ra lỗi. Vui lòng thử lại sau", null, 0)
                }
            } catch (ex: Exception) {
                return Triple("Đã xảy ra lỗi. Vui lòng thử lại sau", null, 0)
            }
        }


        fun SearchWithoutUser(
            prefix: String,
            exclusions: List<Int>
        ): Triple<String, Array<UserModel>?, Int> {
            Log.d("SearchWithout", "SearchWithout")
            var gson = Gson()
            var client = OkHttpClient()
            val request = Request.Builder()
                .url(
                    Common.API_HOST + "api/User/Search?prefix=" + URLEncoder.encode(
                        prefix,
                        "utf-8"
                    ) + "&exclusions=" + URLEncoder.encode(exclusions.joinToString(","))
                )
                .build()
            try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) {
                    var users: Array<UserModel> = gson.fromJson(
                        responseBody,
                        (ArrayList<UserModel>()).toTypedArray().javaClass
                    )
                    Log.d("SearchWithout", "SearchWithout users = ${users.size}")
                    return Triple("", users, 0)
                } else if (statusCode == 400) {
                    if (responseBody == null || responseBody == "") {
                        return Triple("Đã xảy ra lỗi. Vui lòng thử lại sau", null, 0)
                    }
                    return Triple(responseBody, null, 0)
                } else {
                    return Triple("Đã xảy ra lỗi. Vui lòng thử lại sau", null, 0)
                }
            } catch (ex: Exception) {
                return Triple("Đã xảy ra lỗi. Vui lòng thử lại sau", null, 0)
            }
        }

        fun resetPassword(
            userName: String,
            oldPassword: String,
            newPassword: String
        ): String {
            Log.d(
                "TAG",
                "resetPassword: $userName with pass old $oldPassword and new pass $newPassword"
            )
            val data = hashMapOf(
                "userName" to userName,
                "oldPassword" to oldPassword,
                "newPassword" to newPassword
            )
            var Json = MediaType.parse("application/json; charset=utf-8")
            var gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
            var requestBody = RequestBody.create(Json, gson.toJson(data))
            var client = OkHttpClient()
            val request = Request.Builder()
                .url(Common.API_HOST + "api/User/ChangePassword")
                .post(requestBody)
                .build()
            return try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) "Đổi mật khẩu thành công"
                else if (statusCode == 400 && responseBody != null) responseBody
                else if (statusCode == 401) "Phiên đăng nhập của bạn đã hết hạn.";
                else "Đã xảy ra lỗi. Vui lòng thử lại sau."
            } catch (ex: Exception) {
                Log.e("TAG", "resetPassword: Exception = $ex")
                "Đã xảy ra lỗi. Vui lòng thử lại sau.";
            }
        }
        fun getUser(idUser: Int): Triple<String, UserModel?, Int>{
            var gson = Gson()
            var client = OkHttpClient()
            var request = Request.Builder()
                .url(Common.API_HOST + "api/User/GetUser/" + idUser)
                .build()
            try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) {
                    var user: UserModel = gson.fromJson(responseBody, UserModel::class.java)
                    return Triple("", user, 0)
                } else if (statusCode == 400) {
                    if (responseBody == null || responseBody == "") {
                        return Triple("Đã xảy ra lỗi. Vui lòng thử lại sau 1", null, 0)
                    }
                    return Triple(responseBody, null, 0)
                } else {
                    return Triple("Đã xảy ra lỗi. Vui lòng thử lại sau 2", null, 0)
                }
            }
            catch (ex: Exception) {
                return Triple("Đã xảy ra lỗi. Vui lòng thử lại sau 3", null, 0)
            }

        }
    }
}
