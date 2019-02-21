package hu.attilavegh.vbkoveto.service

import android.app.Notification
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import hu.attilavegh.vbkoveto.R
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import hu.attilavegh.vbkoveto.UserActivity
import hu.attilavegh.vbkoveto.controller.NotificationController
import hu.attilavegh.vbkoveto.model.NotificationModel

const val NOTIFICATION_SERVICE_TAG = "FCM Service"

class NotificationService: FirebaseMessagingService() {

    private lateinit var notificationController: NotificationController

    override fun onNewToken(token: String?) {
        super.onNewToken(token)

        Log.d(NOTIFICATION_SERVICE_TAG, "Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val notification = NotificationModel(remoteMessage!!.data["busId"]!!, remoteMessage.data["busName"]!!, remoteMessage.data["title"]!!)
        notificationController = NotificationController(this)

        if (notificationController.hasBus(notification.busId)) {
            createNotification(notification)

            Log.d(NOTIFICATION_SERVICE_TAG, "Notification from: " + remoteMessage.from!!)
            Log.d(NOTIFICATION_SERVICE_TAG, "Notification title: " + remoteMessage.data["title"])
        }
    }

    private fun createNotification(notification: NotificationModel) {
        val notificationText = getString(R.string.notification_text)
        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.notification_channel))
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(notification.title)
            .setContentText(notificationText)
//            .setStyle()
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun createIntent(notification: NotificationModel): PendingIntent {
        val intent = Intent(this, UserActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("notification", true)
        intent.putExtra("busId", notification.busId)
        intent.putExtra("busName", notification.busName)

        return PendingIntent.getActivity(this, 0, intent, 0)
    }
}