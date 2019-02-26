package hu.attilavegh.vbkoveto.utility

import android.app.Activity
import android.widget.FrameLayout
import hu.attilavegh.vbkoveto.R
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView

enum class NotificationDuration(private val time: Long) {
    SHORT(2500), INFINITE(0);

    fun value(): Long {
        return time
    }
}

class NotificationBarUtils(private val activity: Activity) {

    private val notificationBar: FrameLayout = activity.findViewById(R.id.notification_bar)
    private val notificationBarSeparator: View = activity.findViewById(R.id.notification_bar_separator)
    private val notificationBarText: TextView = activity.findViewById(R.id.notification_bar_text)
    private val userContentContainer: FrameLayout = activity.findViewById(R.id.user_fragment_container)

    companion object {
        var isOpen: Boolean = false
    }

    fun show(message: Int, duration: NotificationDuration = NotificationDuration.SHORT) {
        if (isOpen) {
            close()
        }

        isOpen = true
        setDefaultStyle()
        setText(message)
        animate(duration)
    }

    fun show(message: String, duration: NotificationDuration = NotificationDuration.SHORT) {
        setDefaultStyle()
        setText(message)
        animate(duration)
    }

    fun showError(message: Int, duration: NotificationDuration = NotificationDuration.SHORT) {
        setErrorStyle()
        setText(message)
        animate(duration)
    }

    private fun setText(message: Int) {
        notificationBarText.setText(message)
    }

    private fun setText(message: String) {
        notificationBarText.text = message
    }

    private fun animate(duration: NotificationDuration) {
        notificationBar.animate().translationY(ApplicationUtils.dpToPx(56f, activity)).duration = 300
        userContentContainer.animate().translationY(ApplicationUtils.dpToPx(56f, activity)).duration = 300

        if (duration != NotificationDuration.INFINITE) {
            Handler().postDelayed({ hide() }, duration.value())
        }
    }

    private fun setErrorStyle() {
        notificationBar.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorAccent))
        notificationBarSeparator.visibility = View.INVISIBLE
    }

    private fun setDefaultStyle() {
        notificationBar.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        notificationBarSeparator.visibility = View.VISIBLE
    }

    private fun hide(duration: Long = 300) {
        notificationBar.animate().translationY(ApplicationUtils.dpToPx(0f, activity)).duration = duration
        userContentContainer.animate().translationY(ApplicationUtils.dpToPx(0f, activity)).duration = 300

        isOpen = false
        Handler().postDelayed({ setDefaultStyle() }, duration)
    }

    private fun close() {
        notificationBar.translationY = 0f
    }
}