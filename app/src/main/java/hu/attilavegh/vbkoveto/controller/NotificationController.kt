package hu.attilavegh.vbkoveto.controller

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import hu.attilavegh.vbkoveto.model.Bus

const val NOTIFICATION_SHARED_PREFERENCES_FILE_NAME = "hu.attilavegh.vbkoveto.controller.notification"
const val NOTIFICATION_ACTIVE = "active"

class NotificationController(context: Context) {

    private var notificationSharedPreferences = context.getSharedPreferences(NOTIFICATION_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    fun enable() {
        FirebaseMessaging.getInstance().subscribeToTopic("departure")

        val preferencesEditor = notificationSharedPreferences.edit()

        preferencesEditor.putString(NOTIFICATION_ACTIVE, "")
        preferencesEditor.apply()
    }

    fun disable() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("departure")

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

    fun hasBus(bus: Bus): Boolean {
        return notificationSharedPreferences.contains(bus.id)
    }
}