package com.example.kltn.services

import com.example.kltn.Common
import com.example.kltn.GroupInfos
import com.example.kltn.models.GroupModel
import com.example.kltn.models.UserModel
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class GroupService {
    companion object {
        fun getMember(id: Int): List<UserModel> {
            var member = UserModel(1, "user1")
            member.firstname = "A"
            member.lastname = "Nguyễn Văn"
            return listOf<UserModel>(member)
        }

        fun create(userId: Int, name: String): Triple<String, Int, Int> {
            var Json = MediaType.parse("application/json; charset=utf-8")
            var group = GroupInfos()
            group.Name = name
            group.Creator.Id = userId
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
                }
                else {
                    return Triple(Common.NOT_PERMIT, 0, 0)
                }
            }
            catch (ex: Exception) {
                return Triple(Common.ERR_MSG, 0, 0)
            }
        }
    }
}