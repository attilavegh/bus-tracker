package hu.attilavegh.vbkoveto.utility

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class VibratorUtils(activity: Activity) {
    private val vibrator: Vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun vibrate(time: Long) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(time)
    }
}