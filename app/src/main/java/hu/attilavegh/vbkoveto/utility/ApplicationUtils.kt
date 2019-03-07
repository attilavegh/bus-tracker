package hu.attilavegh.vbkoveto.utility

import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.TypedValue
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.Timestamp
import hu.attilavegh.vbkoveto.R
import java.text.SimpleDateFormat
import java.util.*

private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

class ApplicationUtils {
    companion object {
        fun isAppForeground(): Boolean {
            val appProcessInfo = ActivityManager.RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(appProcessInfo)

            return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
        }

        fun dpToPx(dp: Float, context: Context): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
        }

        fun pxToDp(px: Float, context: Context): Float {
            return px / context.resources.displayMetrics.density
        }

        fun createDisplayTime(millis: Long): String {
            return SimpleDateFormat("HH:mm", Locale.GERMANY).format(Date(millis))
        }

        fun createDisplayTime(timestamp: Timestamp): String {
            return SimpleDateFormat("HH:mm", Locale.GERMANY).format(timestamp.toDate())
        }

        fun checkPlayServices(activity: Activity): Boolean {
            val googleAPI = GoogleApiAvailability.getInstance()
            val result = googleAPI.isGooglePlayServicesAvailable(activity)

            if (result != ConnectionResult.SUCCESS) {
                if (googleAPI.isUserResolvableError(result)) {
                    googleAPI.getErrorDialog(activity, result, PLAY_SERVICES_RESOLUTION_REQUEST).show()
                }

                return false
            }

            return true
        }

        fun createNotificationChannel(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val id = activity.getString(R.string.notification_channelId)
                val name = activity.getString(R.string.notification_channelName)
                val importance = NotificationManager.IMPORTANCE_HIGH

                val channel = NotificationChannel(id, name, importance)

                val notificationManager = activity.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}