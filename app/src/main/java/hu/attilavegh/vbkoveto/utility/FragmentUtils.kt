package hu.attilavegh.vbkoveto.utility

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import hu.attilavegh.vbkoveto.R

class FragmentUtils(private val fragmentManager: FragmentManager) {

    fun switchTo(container: Int, fragment: Fragment, bundle: Bundle = Bundle.EMPTY) {
        fragment.arguments = bundle

        fragmentManager.popBackStack()
        fragmentManager.beginTransaction()
            .replace(container, fragment)
            .commit()
    }

    fun switchTo(container: Int, fragment: Fragment, tag: String, bundle: Bundle = Bundle.EMPTY) {
        fragment.arguments = bundle

        fragmentManager.beginTransaction()
            .replace(container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }
}