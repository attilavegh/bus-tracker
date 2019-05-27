package hu.attilavegh.vbkoveto.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import hu.attilavegh.vbkoveto.R
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import hu.attilavegh.vbkoveto.UserActivity
import hu.attilavegh.vbkoveto.controller.NotificationController
import hu.attilavegh.vbkoveto.model.NotificationModel
import hu.attilavegh.vbkoveto.model.UserModel
import hu.attilavegh.vbkoveto.utility.ApplicationUtils
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class NotificationService: FirebaseMessagingService() {

    private lateinit var notificationController: NotificationController
    private lateinit var authenticationService: AuthenticationService

    override fun onCreate() {
        super.onCreate()
        notificationController = NotificationController(this)
        authenticationService = AuthenticationService(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val user = authenticationService.getUser()
        val notification = parseNotificationData(remoteMessage)

        if (notification.type == "arrival") {
            removeOutdatedNotification(notification)
        }

        if (canReceiveNotification(notification, user)) {
            createNotification(notification)
        } else if (canReceiveInAppNotification(notification, user)) {
            createInAppNotification(notification)
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

    private fun createInAppNotification(notification: NotificationModel) {
        val intent = Intent("inAppNotificationReceiver")
        intent.putExtra("busId", notification.busId)
        intent.putExtra("busName", notification.busName)
        intent.putExtra("title", notification.title)

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun parseNotificationData(remoteMessage: RemoteMessage?): NotificationModel {
        return NotificationModel(
            remoteMessage!!.data["busId"]!!.hashCode(),
            remoteMessage.data["type"]!!,
            remoteMessage.data["busId"]!!,
            remoteMessage.data["busName"]!!,
            remoteMessage.data["title"]!!
        )
    }

    private fun canReceiveNotification(notification: NotificationModel, user: UserModel): Boolean {
        return notificationController.hasBus(notification.busId)
                && !ApplicationUtils.isAppForeground()
                && !user.isDriver
                && notification.type == "departure"
    }

    private fun canReceiveInAppNotification(notification: NotificationModel, user: UserModel): Boolean {
        return notificationController.hasBus(notification.busId)
                && ApplicationUtils.isAppForeground()
                && !user.isDriver
                && notification.type == "departure"
    }

    private fun removeOutdatedNotification(notification: NotificationModel) {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.cancel(notification.id)
    }

    private fun createIntent(notification: NotificationModel): PendingIntent {
        val pendingIntentFlags = PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_UPDATE_CURRENT
        val intent = Intent(this, UserActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        intent.putExtra("notification", true)
        intent.putExtra("busId", notification.busId)
        intent.putExtra("busName", notification.busName)

        return PendingIntent.getActivity(this, 0, intent, pendingIntentFlags)
    }
}