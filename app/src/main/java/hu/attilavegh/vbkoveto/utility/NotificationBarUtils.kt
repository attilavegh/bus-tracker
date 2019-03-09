package hu.attilavegh.vbkoveto.utility

import android.app.Activity
import android.content.Intent
import android.widget.FrameLayout
import hu.attilavegh.vbkoveto.R
import android.os.Handler
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import hu.attilavegh.vbkoveto.model.Bus
import android.view.GestureDetector.SimpleOnGestureListener

@Suppress("ClickableViewAccessibility")
class NotificationBarUtils(private val activity: Activity) : View.OnTouchListener {
    private val openPosition: Float = 0f
    private val closePosition: Float = -70f
    private val displayTime: Long = 3000

    private val notificationBar: FrameLayout = activity.findViewById(R.id.notification_bar)
    private val notificationBarText: TextView = activity.findViewById(R.id.notification_bar_text)

    private val closeHandler = Handler()
    private val closer: Runnable = Runnable { close() }
    private val gestureDetector = GestureDetector(activity, SingleTap())
    private var deltaY: Float = 0f

    private var clickableNotification = true
    private var message = ""
    private var bus = Bus()

    companion object {
        var isOpen: Boolean = false
    }

    init {
        notificationBar.setOnTouchListener(this)
    }

    private inner class SingleTap: SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent): Boolean {
            return true
        }
    }

    override fun onTouch(view: View, motion: MotionEvent): Boolean {
        val y = motion.rawY

        if (gestureDetector.onTouchEvent(motion) && clickableNotification) {
            onNotificationClick()
            return true
        }

        when (motion.action) {
            MotionEvent.ACTION_DOWN -> {
                deltaY = y
                closeHandler.removeCallbacks(closer)
            }
            MotionEvent.ACTION_MOVE -> {
                if (y <= deltaY) {
                    notificationBar.y = -(deltaY - y)
                }
            }
            MotionEvent.ACTION_UP -> {
                close(100)
            }
        }

        return false
    }

    fun show(message: String, bus: Bus) {
        clickableNotification = true
        this.message = message
        this.bus = bus

        open()
    }

    fun show(message: Int) {
        clickableNotification = false
        this.message = activity.getString(message)

        open()
    }

    private fun open(duration: Long = 300) {
        if (isOpen) {
            closeHandler.removeCallbacks(closer)

            setText(message)
            closeHandler.postDelayed(closer, displayTime)
        } else {
            isOpen = true
            setText(message)

            notificationBar.animate().translationY(ApplicationUtils.dpToPx(openPosition, activity)).duration = duration
            closeHandler.postDelayed(closer, displayTime)
        }
    }

    private fun close(duration: Long = 300) {
        isOpen = false
        notificationBar.animate().translationY(ApplicationUtils.dpToPx(closePosition, activity)).duration = duration
    }

    private fun setText(message: String) {
        notificationBarText.text = message
    }

    private fun onNotificationClick() {
        closeHandler.removeCallbacks(closer)
        close()

        val intent = Intent("inAppNotificationHandler")
        intent.putExtra("busId", bus.id)
        intent.putExtra("busName", bus.name)

        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent)
    }
}