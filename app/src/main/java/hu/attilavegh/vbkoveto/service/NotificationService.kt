package hu.attilavegh.vbkoveto.service

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import hu.attilavegh.vbkoveto.R
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import hu.attilavegh.vbkoveto.UserActivity
import hu.attilavegh.vbkoveto.controller.AuthController
import hu.attilavegh.vbkoveto.controller.NotificationController
import hu.attilavegh.vbkoveto.model.NotificationModel
import hu.attilavegh.vbkoveto.utility.ApplicationUtils

const val NOTIFICATION_SERVICE_TAG = "FCM Service"

class NotificationService : FirebaseMessagingService() {

    private lateinit var notificationController: NotificationController
    private lateinit var authController: AuthController

    override fun onNewToken(token: String?) {
        super.onNewToken(token)

        Log.d(NOTIFICATION_SERVICE_TAG, "Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val notification = parseNotificationData(remoteMessage)

        if (notification.type == "arrival") {
            removeOutdatedNotification(notification)
        }

        if (canReceiveNotification(notification)) {
            createNotification(notification)
        }
    }

    private fun createNotification(notification: NotificationModel) {
        val intent = createIntent(notification)
        val notificationText = getString(R.string.notification_text)

        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.notification_channelId))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(notification.title)
            .setContentText(notificationText)
            .setContentIntent(intent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notification.id, notificationBuilder.build())
    }

    private fun parseNotificationData(remoteMessage: RemoteMessage?): NotificationModel {
        return NotificationModel(
            remoteMessage!!.data["notificationId"]!!.toInt(),
            remoteMessage.data["type"]!!,
            remoteMessage.data["busId"]!!,
            remoteMessage.data["busName"]!!,
            remoteMessage.data["title"]!!
        )
    }

    private fun canReceiveNotification(notification: NotificationModel): Boolean {
        notificationController = NotificationController(this)
        authController = AuthController(this)

        val user = authController.getUser()

        return notificationController.hasBus(notification.busId)
                && !ApplicationUtils.isAppForeground()
                && !user.isDriver
                && notification.type == "departure"
    }

    private fun removeOutdatedNotification(notification: NotificationModel) {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.cancel(notification.id)

    }

    private fun createIntent(notification: NotificationModel): PendingIntent {
        val intent = Intent(this, UserActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        intent.putExtra("notification", true)
        intent.putExtra("busId", notification.busId)
        intent.putExtra("busName", notification.busName)

        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}