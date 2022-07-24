package com.example.kltn.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.kltn.services.GroupService
import com.example.kltn.services.UserGroupService
import com.example.kltn.utils.Constants
import com.example.kltn.worker.notification.AcceptGroupNotification

class NotificationWorker(private val ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result = try {
        Log.d("TAG", "doWork: ")
        val userId = inputData.getInt(Constants.USER_ID, 0)
        Log.d("TAG", "doWork: userId = $userId")
        val listInvitation = UserGroupService.GetInvitation(userId)
        Log.d("TAG", "doWork: listInvitation = ${listInvitation.second?.size}")
        listInvitation.second?.forEach {
            Log.d("TAG", "doWork: id group = ${it.group.id}")
            AcceptGroupNotification(context = ctx).acceptRequest(
                userId,
                it.group.id, it.group.name,
                it.group.id
            )
        }
        Result.success()
    } catch (e: Throwable) {
        Log.d("TAG", "doWork: ")
        Result.failure()
    }

    companion object {
        const val INVITATION = "INVITATION"
        const val ID_GROUP = "ID_GROUP"
        const val ID_USER = "ID_USER"
    }
}


