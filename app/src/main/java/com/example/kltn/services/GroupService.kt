package com.example.kltn.services

import android.util.Log
import com.example.kltn.*
import com.example.kltn.models.GroupModel
import com.example.kltn.models.UserModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.ArrayList

class GroupService {
    companion object {
        fun getMember(id: Int): Triple<String, Array<UserGroupInfos>?, Int> {
            var gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
            var client = OkHttpClient()
            val request = Request.Builder()
                .url(Common.API_HOST + "api/UserGroup/GetAllMember/" + id)
                .build()
            try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) {
                    var usergroups: Array<UserGroupInfos> = gson.fromJson(
                        responseBody,
                        (ArrayList<UserGroupInfos>()).toTypedArray().javaClass
                    )
                    return Triple("", usergroups, 0)
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

        fun deleteGroup(idGroup: Int): Triple<String, Int, Int> {
            Log.d("TAG", "deleteGroup111: id group = $idGroup")
            var Json = MediaType.parse("application/json; charset=utf-8")
            var client = OkHttpClient()
            var request = Request.Builder()
                .url(Common.API_HOST + "api/Group/Delete/" + idGroup)
                .build()
            try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) {
                    return Triple("Xóa group thành công !", responseBody!!.toInt(), 0)
                }
                if (statusCode == 400) {
                    if (responseBody == null || responseBody == "") {
                        return Triple(Common.ERR_MSG, 0, 0)
                    }
                    return Triple(responseBody, 0, 0)
                }
                if (statusCode == 401) {
                    return Triple(Common.UNAUTHORIZED, 0, 0)
                } else {
                    return Triple(Common.NOT_PERMIT, 0, 0)
                }
            } catch (ex: Exception) {
                Log.d("TAG", "deleteGroup: $ex")
                return Triple(Common.ERR_MSG, 0, 0)
            }
        }

        fun create(userId: Int, name: String): Triple<String, Int, Int> {
            var Json = MediaType.parse("application/json; charset=utf-8")
            var group = GroupInfos()
            group.name = name
            group.creator.id = userId
            var gson = Gson()
            var client = OkHttpClient()
            var requestBody = RequestBody.create(Json, gson.toJson(group))
            var request = Request.Builder()
                .url(Common.API_HOST + "api/Group/Update")
                .post(requestBody)
                .build()
            try {
                var response = client.newCall(request).execute()
                var statusCode = response.code()
                var responseBody = response.body()?.string()
                if (statusCode == 200) {
                    return Triple("", responseBody!!.toInt(), 0)
                }
                if (statusCode == 400) {
                    if (responseBody == null || responseBody == "") {
                        return Triple(Common.ERR_MSG, 0, 0)
                    }
                    return Triple(responseBody, 0, 0)
                }
                if (statusCode == 401) {
                    return Triple(Common.UNAUTHORIZED, 0, 0)
                } else {
                    return Triple(Common.NOT_PERMIT, 0, 0)
                }
            } catch (ex: Exception) {
                return Triple(Common.ERR_MSG, 0, 0)
            }
        }
    }

    fun changeNameGroup(idGroup: Int, name: String): Triple<String, Int, Int> {
        var Json = MediaType.parse("application/json; charset=utf-8")
       val data = hashMapOf("id" to idGroup, "name" to name)
        var gson = Gson()
        var client = OkHttpClient()
        var requestBody = RequestBody.create(Json, gson.toJson(data))
        var request = Request.Builder()
            .url(Common.API_HOST + "api/Group/ChangeName")
            .post(requestBody)
            .build()
        try {
            var response = client.newCall(request).execute()
            var statusCode = response.code()
            var responseBody = response.body()?.string()
            if (statusCode == 200) {
                return Triple("Đã đổi tên nhóm thành công", responseBody!!.toInt(), 0)
            }
            if (statusCode == 400) {
                if (responseBody == null || responseBody == "") {
                    return Triple(Common.ERR_MSG, 0, 0)
                }
                return Triple(responseBody, 0, 0)
            }
            if (statusCode == 401) {
                return Triple(Common.UNAUTHORIZED, 0, 0)
            } else {
                return Triple(Common.NOT_PERMIT, 0, 0)
            }
        } catch (ex: Exception) {
            return Triple(Common.ERR_MSG, 0, 0)
        }
    }
}
