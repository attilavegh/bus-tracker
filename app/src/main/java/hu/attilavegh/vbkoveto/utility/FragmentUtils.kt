package hu.attilavegh.vbkoveto.utility

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

import hu.attilavegh.vbkoveto.R

class FragmentUtils(private val fragmentManager: FragmentManager) {

    fun switchTo(fragment: Fragment, bundle: Bundle = Bundle.EMPTY) {
        fragment.arguments = bundle

        fragmentManager.popBackStack()
        fragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    fun switchTo(fragment: Fragment, tag: String, bundle: Bundle = Bundle.EMPTY) {
        fragment.arguments = bundle

        fragmentManager.beginTransaction()
            .replace(R.id.container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }
}