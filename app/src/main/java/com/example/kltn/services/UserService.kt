package com.example.kltn.services

import android.util.Log
import com.example.kltn.models.UserModel

class UserService {
    companion object {
        fun SearchWithout(prefix: String, exclusions: List<Int>): List<UserModel> {
            var user1 = UserModel(1, "user1")
            user1.firstname = "A"
            user1.lastname = "Nguyễn Văn"
            user1.phone = "0123456781"

            var user2 = UserModel(2, "user2")
            user2.firstname = "B"
            user2.lastname = "Nguyễn Văn"
            user2.phone = "0123456782"

            var user3 = UserModel(3, "user3")
            user3.firstname = "C"
            user3.lastname = "Nguyễn Văn"
            user3.phone = "0123456783"

            var user4 = UserModel(4, "user4")
            user4.firstname = "D"
            user4.lastname = "Nguyễn Văn"
            user4.phone = "0123456784"

            var user5 = UserModel(5, "user5")
            user5.firstname = "E"
            user5.lastname = "Nguyễn Văn"
            user5.phone = "0123456785"

            var user6 = UserModel(6, "member6")
            user6.firstname = "X"
            user6.lastname = "Lê Thị"
            user6.phone = "0987654329"

            var user7 = UserModel(7, "member7")
            user7.firstname = "Y"
            user7.lastname = "Lê Thị"
            user7.phone = "0987654328"

            var user8 = UserModel(8, "member8")
            user8.firstname = "Z"
            user8.lastname = "Lê Thị"
            user8.phone = "0987654327"

            var user9 = UserModel(9, "member9")
            user9.firstname = "T"
            user9.lastname = "Lê Thị"
            user9.phone = "0987654326"

            var user10 = UserModel(10, "member10")
            user10.firstname = "V"
            user10.lastname = "Lê Thị"
            user10.phone = "0987654325"

            var users = listOf(user1, user2, user3, user4, user5, user6, user7, user8, user9, user10)
            var filterUsers = users.filter { !exclusions.contains(it.id) }
            filterUsers = filterUsers.filter { it.username.contains(prefix) || (it.lastname + " " + it.firstname).contains(prefix) || it.phone.contains(prefix) }
            filterUsers = filterUsers.sortedWith(compareBy({ it.username }, { it.firstname }, { it.lastname }))
            if (filterUsers.count() > 3) filterUsers = filterUsers.subList(0, 3)
            var result = ArrayList<UserModel>()
            for (user in filterUsers) result.add(user)

            return result
        }
    }
}