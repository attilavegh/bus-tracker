package hu.attilavegh.vbkoveto.utilities

import android.support.v7.widget.Toolbar

class ActivityTitleUtils(private val toolbar: Toolbar) {

    private var previous: String = ""
    private var current: String = ""

    fun set(title: String) {
        previous = current
        current = title

        toolbar.title = title
    }

    fun setPrevious() {
        set(previous)
    }
}