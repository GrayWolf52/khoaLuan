package com.example.kltn.models

class UserGroupModel {
    var userId: Int = 0
    var groupId: Int = 0
    var roleId: Int = 0
    var user: UserModel? = null
    var group: GroupModel? = null
    var role: RoleModel? = null
}