package hu.attilavegh.vbkoveto.utility

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.widget.Toast

class ToastUtils(private val context: Context, private val resources: Resources) {

    private var openToast: Toast = Toast(context)

    fun create(messageResource: Int, offset: Int = 100) {
        closeOpenToast()

        val toast: Toast = Toast.makeText(context, messageResource, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, offset * resources.displayMetrics.density.toInt())

        show(toast)
        saveToastReference(toast)
    }

    fun createLong(messageResource: Int, offset: Int = 100) {
        closeOpenToast()

        val toast: Toast = Toast.makeText(context, messageResource, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM, 0, offset * resources.displayMetrics.density.toInt())

        show(toast)
        saveToastReference(toast)
    }

    fun create(messageResource: String, offset: Int = 100) {
        closeOpenToast()

        val toast: Toast = Toast.makeText(context, messageResource, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, offset * resources.displayMetrics.density.toInt())

        show(toast)
        saveToastReference(toast)
    }

    fun closeOpenToast() {
        openToast.cancel()
    }

    private fun show(toast: Toast) {
        if (ApplicationUtils.isAppForeground()) {
            toast.show()
        }
    }

    private fun saveToastReference(toast: Toast) {
        openToast = toast
    }
}