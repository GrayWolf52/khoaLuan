package com.example.kltn.services

import com.example.kltn.models.GroupModel

class GroupService {
    companion object {
        fun get(): List<GroupModel> {
            var group = GroupModel(1)
            group.name = "Nhóm 1"
            return listOf<GroupModel>(group)
        }
    }
}