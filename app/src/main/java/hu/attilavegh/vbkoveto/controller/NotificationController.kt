package hu.attilavegh.vbkoveto.controller

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import hu.attilavegh.vbkoveto.model.Bus

const val NOTIFICATION_SHARED_PREFERENCES_FILE_NAME = "hu.attilavegh.vbkoveto.controller.notification"
const val NOTIFICATION_ACTIVE = "active"

const val START_SHARED_PREFERENCES_FILE_NAME = "hu.attilavegh.vbkoveto.controller.start"
const val FIRST_START = "firstStart"

const val NOTIFICATION_TOPIC = "bus_notification"

class NotificationController(private val context: Context) {

    private var notificationSharedPreferences = context.getSharedPreferences(NOTIFICATION_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
    private var startSharedPreferences = context.getSharedPreferences(START_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    fun enable() {
        FirebaseMessaging.getInstance().subscribeToTopic(NOTIFICATION_TOPIC)

        val preferencesEditor = notificationSharedPreferences.edit()

        preferencesEditor.putString(NOTIFICATION_ACTIVE, "")
        preferencesEditor.apply()
    }

    fun disable() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(NOTIFICATION_TOPIC)

        val preferencesEditor = notificationSharedPreferences.edit()

        preferencesEditor.clear()
        preferencesEditor.apply()
    }

    fun isEnabled(): Boolean {
        return notificationSharedPreferences.contains(NOTIFICATION_ACTIVE)
    }

    fun add(bus: Bus) {
        bus.favorite = true

        val preferencesEditor = notificationSharedPreferences.edit()
        preferencesEditor.putString(bus.id, "")
        preferencesEditor.apply()
    }

    fun remove(bus: Bus) {
        if (notificationSharedPreferences.contains(bus.id)) {
            bus.favorite = false

            val preferencesEditor = notificationSharedPreferences.edit()
            preferencesEditor.remove(bus.id)
            preferencesEditor.apply()
        }
    }

    fun hasBus(busId: String): Boolean {
        return notificationSharedPreferences.contains(busId)
    }

    fun removeAllNotifications() {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancelAll()
    }

    fun markFirstStart() {
        val preferencesEditor = startSharedPreferences.edit()

        preferencesEditor.putBoolean(FIRST_START, false)
        preferencesEditor.apply()
    }

    fun isFirstStart(): Boolean {
        return !startSharedPreferences.contains(FIRST_START)
    }
}