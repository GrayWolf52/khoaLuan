package com.example.kltn.services

import com.example.kltn.models.GroupModel
import com.example.kltn.models.UserModel

class GroupService {
    companion object {
        fun get(): List<GroupModel> {
            var group = GroupModel(1)
            group.name = "Nhóm 1"
            return listOf<GroupModel>(group)
        }

        fun getMember(id: Int): List<UserModel> {
            var member = UserModel(1, "user1")
            member.firstname = "A"
            member.lastname = "Nguyễn Văn"
            return listOf<UserModel>(member)
        }

        fun create(name: String): Boolean {
            return true
        }
    }
}