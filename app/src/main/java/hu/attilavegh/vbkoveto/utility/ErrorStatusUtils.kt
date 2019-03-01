package hu.attilavegh.vbkoveto.utility

import android.app.Activity
import android.os.Handler
import android.widget.FrameLayout
import android.widget.TextView
import hu.attilavegh.vbkoveto.R

class ErrorStatusUtils(private val activity: Activity) {
    private val openPosition: Float = 0f
    private val closePosition: Float = -56f
    private val animationDuration: Long = 300
    private val displayTime: Long = 2000

    private val closeHandler = Handler()
    private val closer: Runnable = Runnable { hide() }

    private val errorStatusBar: FrameLayout = activity.findViewById(R.id.error_status)
    private val errorStatusBarText: TextView = activity.findViewById(R.id.error_status_text)

    companion object {
        var isOpen: Boolean = false
    }

    fun show(message: Int, drawable: Int) {
        if (isOpen) {
            closeHandler.removeCallbacks(closer)

            setDrawable(drawable)
            setText(message)
            closeHandler.postDelayed(closer, displayTime)
        } else {
            isOpen = true
            setDrawable(drawable)
            setText(message)

            errorStatusBar.animate().translationY(ApplicationUtils.dpToPx(openPosition, activity)).duration = animationDuration
            closeHandler.postDelayed(closer, displayTime)
        }
    }

    fun hide() {
        isOpen = false
        errorStatusBar.animate().translationY(ApplicationUtils.dpToPx(closePosition, activity)).duration = animationDuration
    }

    private fun setText(id: Int) {
        errorStatusBarText.setText(id)
    }

    private fun setDrawable(id: Int) {
        errorStatusBarText.setCompoundDrawablesWithIntrinsicBounds(id, 0,0,0)
    }
}