package hu.attilavegh.vbkoveto.utility

import androidx.appcompat.widget.Toolbar

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