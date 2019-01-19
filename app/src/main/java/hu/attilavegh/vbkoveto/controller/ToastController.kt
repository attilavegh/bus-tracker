package hu.attilavegh.vbkoveto.controller

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.widget.Toast

class ToastController(private val context: Context, private val resources: Resources) {

    fun create(messageResource: Int) {
        val toast: Toast = Toast.makeText(context, messageResource, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 100 * resources.displayMetrics.density.toInt())
        toast.show()
    }

    fun create(messageResource: String) {
        val toast: Toast = Toast.makeText(context, messageResource, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 100 * resources.displayMetrics.density.toInt())
        toast.show()
    }
}