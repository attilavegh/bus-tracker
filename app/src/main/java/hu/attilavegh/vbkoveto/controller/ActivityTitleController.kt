package hu.attilavegh.vbkoveto.controller

import android.support.v7.widget.Toolbar

class ActivityTitleController(private val toolbar: Toolbar) {

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