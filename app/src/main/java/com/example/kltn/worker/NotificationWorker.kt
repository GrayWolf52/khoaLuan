package com.example.kltn.worker

import android.content.Context
import android.util.Log
import androidx.work.WorkInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.kltn.services.GroupService
import com.example.kltn.services.UserGroupService
import com.example.kltn.utils.Constants

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result = try {
        Log.d("TAG", "doWork: ")
        val userId = inputData.getInt(Constants.USER_ID, 0)
        Log.d("TAG", "doWork: userId = $userId")
        val listInvitation = UserGroupService.GetInvitation(userId)
        Log.d("TAG", "doWork: listInvitation = ${listInvitation.second?.size}")
        val invitation = listInvitation.second?.findLast {
            !it.isAccepted
        }
        Log.d("TAG", "doWork: invitation = ${invitation}")

        setProgressAsync(
            workDataOf(
                INVITATION to invitation?.group?.name,
                ID_GROUP to invitation?.group?.id,
                ID_USER to invitation?.user?.id
            )
        )
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


