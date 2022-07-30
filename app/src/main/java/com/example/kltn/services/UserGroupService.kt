package com.example.kltn.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.kltn.*
import com.example.kltn.models.EventModel
import com.example.kltn.models.UserGroupModel
import com.example.kltn.models.UserModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UserGroupService {
    companion object {
        var loadDataAgain = MutableLiveData(false)
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
                    var userGroups: Array<UserGroupModel> = gson.fromJson(
                        responseBody,
                        (ArrayList<UserGroupModel>()).toTypedArray().javaClass
                    )
                    Log.d("userGroups", " userGroups size = ${userGroups.size}")
                    return Triple("", userGroups, 0)
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


        fun GetInvitation(userId: Int): Triple<String, Array<UserGroupInfos>?, Int> {
            Log.d("TAG", "GetInvitation: userId = $userId")
            var client = OkHttpClient()
            var gson = Gson()
            val request = Request.Builder()
                .url(Common.API_HOST + "api/UserGroup/GetInvitation/" + userId)
                .build()
            try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) {
                    var userGroups: Array<UserGroupInfos> = gson.fromJson(
                        responseBody,
                        (ArrayList<UserGroupInfos>()).toTypedArray().javaClass
                    )
                    Log.d("userGroups", " userGroups size = ${userGroups.size}")
                    return Triple("", userGroups, 0)
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


        fun acceptInvitation(
            userId: Int,
            groupId: Int,
            isAccecpt: Boolean
        ): String {
            val data = hashMapOf("userId" to userId, "groupId" to groupId, "accept" to isAccecpt)
            var Json = MediaType.parse("application/json; charset=utf-8")
            var gson = Gson()
            var requestBody = RequestBody.create(Json, gson.toJson(data))
            var client = OkHttpClient()
            val request = Request.Builder()
                .url(Common.API_HOST + "api/UserGroup/AcceptInvitation")
                .post(requestBody)
                .build()
            return try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) "Tham gia nhom thanh cong"
                else if (statusCode == 400 && responseBody != null) responseBody
                else if (statusCode == 401) "Phiên đăng nhập của bạn đã hết hạn.";
                else "Đã xảy ra lỗi. Vui lòng thử lại sau."
            } catch (ex: Exception) {
                Log.e("TAG", "acceptInvitation: Exception = $ex")
                "Đã xảy ra lỗi. Vui lòng thử lại sau.";
            }
        }


/*

        fun GetForAll(groupId: Int): Triple<String, Array<UserGroupModel>?, Int> {
            var client = OkHttpClient()
            var gson = Gson()
            val request = Request.Builder()
                .url(Common.API_HOST + "api/UserGroup/GetAllMember/" + groupId)
                .build()
            try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) {
                    var userGroups: Array<UserGroupModel> = gson.fromJson(
                        responseBody,
                        (ArrayList<UserGroupModel>()).toTypedArray().javaClass
                    )
                    Log.d("userGroups", " userGroups size = ${userGroups.size}")
                    return Triple("", userGroups, 0)
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
*/

        fun addUser(selectedMember: MutableList<Int>, groupId: Int): Triple<String, Int, Int> {
            Log.d("TAG", "addUser: groupid = $groupId ")
            var Json = MediaType.parse("application/json; charset=utf-8")
            var gson = Gson()
            val listData = mutableListOf<HashMap<String, Int>>()
            selectedMember.forEach {
                listData.add(hashMapOf("Key" to it, "Value" to 1))
            }
            var requestBody = RequestBody.create(Json, gson.toJson(listData))
            var client = OkHttpClient()
            var request = Request.Builder()
                .url(Common.API_HOST + "api/UserGroup/Add/" + groupId)
                .post(requestBody)
                .build()
            try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) {
                    Log.d("TAG", "addUser:success ")
                    loadDataAgain.postValue(true)
                    return Triple("Gửi lời mời tham gia nhóm thành công.", 0, 0)
                }
                if (statusCode == 400) {
                    if (responseBody == null || responseBody == "") {
                        Log.d("TAG", "addUser:statusCode == 400 ")
                        return Triple(Common.ERR_MSG, 0, 0)
                    }
                    return Triple(responseBody, 0, 0)
                }
                if (statusCode == 401) {
                    Log.d("TAG", "addUser:statusCode == 401 ")
                    return Triple(Common.UNAUTHORIZED, 0, 0)
                } else {
                    Log.d("TAG", "addUser:statusCode != 401 ")
                    return Triple(Common.NOT_PERMIT, 0, 0)
                }
            } catch (ex: Exception) {
                return Triple(Common.ERR_MSG, 0, 0)
            }
        }
    }
}