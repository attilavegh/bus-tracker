package hu.attilavegh.vbkoveto.utility

import android.app.ActivityManager
import android.content.Context
import android.util.TypedValue

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
    }
}