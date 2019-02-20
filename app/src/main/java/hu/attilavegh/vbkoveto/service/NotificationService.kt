package hu.attilavegh.vbkoveto.service

import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import hu.attilavegh.vbkoveto.R
import android.app.PendingIntent
import android.content.Intent
import hu.attilavegh.vbkoveto.UserActivity
import android.support.v4.app.NotificationManagerCompat
import hu.attilavegh.vbkoveto.model.Bus

const val NOTIFICATION_SERVICE_TAG = "FCM Service"

class NotificationService: FirebaseMessagingService() {

    override fun onNewToken(token: String?) {
        super.onNewToken(token)

        Log.d(NOTIFICATION_SERVICE_TAG, "Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        createNotification(remoteMessage!!)

        Log.d(NOTIFICATION_SERVICE_TAG, "From: " + remoteMessage.from!!)
        Log.d(NOTIFICATION_SERVICE_TAG, "Notification Message Body: " + remoteMessage.notification!!.body!!)
    }

    private fun createNotification(message: RemoteMessage) {
        val notificationText = getString(R.string.notification_text)
        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.notification_channel))
            .setContentTitle(message.notification!!.body)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun createIntent(busId: String, busName: String): PendingIntent {
        val intent = Intent(this, UserActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("notification", true)
        intent.putExtra("busId", busId)
        intent.putExtra("busName", busName)

        return PendingIntent.getActivity(this, 0, intent, 0)
    }
}