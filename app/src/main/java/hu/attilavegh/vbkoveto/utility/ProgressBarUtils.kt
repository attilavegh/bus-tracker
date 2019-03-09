package hu.attilavegh.vbkoveto.utility

import android.app.Activity
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ProgressBar
import hu.attilavegh.vbkoveto.R

class ProgressBarUtils(activity: Activity) {

    private val progressBar = activity.findViewById<ProgressBar>(R.id.progress_bar)

    init {
        progressBar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN)
    }

    fun show() {
        progressBar.visibility = View.VISIBLE
    }

    fun hide() {
        progressBar.visibility = View.INVISIBLE
    }
}