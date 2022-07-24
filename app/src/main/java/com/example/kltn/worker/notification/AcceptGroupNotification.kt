package com.example.kltn.worker.notification

import android.R
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.kltn.utils.Constants
import com.example.kltn.worker.broadcast.AcceptGroupBroadcast

class AcceptGroupNotification(private var context: Context) {

    @SuppressLint("ServiceCast")
    fun acceptRequest(userId: Int, groupId: Int, groupName: String?, idNotification: Int) {
        Log.d(
            "TAG",
            "acceptRequest() called with: userId = $userId, groupId = $groupId, groupName = $groupName, idNotification = $idNotification"
        )
        val notificationIntent = Intent(context, AcceptGroupBroadcast::class.java)
        val yesIntent = PendingIntent.getBroadcast(
            context, groupId, notificationIntent.apply {
                action = Constants.ACTION_YES
                putExtra(Constants.USER_ID, userId)
                putExtra(Constants.GROUD_ID, groupId)
                putExtra(Constants.NOTIFICATION_ID, idNotification)
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val noIntent = PendingIntent.getBroadcast(
            context, groupId, notificationIntent.apply {
                action = Constants.ACTION_NO
                putExtra(Constants.USER_ID, userId)
                putExtra(Constants.GROUD_ID, groupId)
                putExtra(Constants.NOTIFICATION_ID, idNotification)
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context)
            .setContentTitle("Lời mời tham gia nhóm")
            .setContentText("Bạn có muốn tham gia nhóm $groupName này không?")
            .addAction(R.drawable.alert_dark_frame, "Có", yesIntent)
            .addAction(R.drawable.alert_dark_frame, "Không ", noIntent)
            .setContentIntent(noIntent).setSmallIcon(R.drawable.ic_input_add)
            .setAutoCancel(true)

        // Add as notification

        // Add as notification

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder.build())

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    "NOTIFICATION_CHANNEL_ID",
                    "NOTIFICATION_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_HIGH
                );
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            builder.setChannelId("NOTIFICATION_CHANNEL_ID");
            manager.createNotificationChannel(notificationChannel);
        }
        manager.notify(idNotification, builder.build())
    }
}