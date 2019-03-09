package hu.attilavegh.vbkoveto.utility

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class FragmentUtils(private val fragmentManager: FragmentManager) {

    fun switchToMainFragment(container: Int, fragment: Fragment) {
        fragmentManager.popBackStack()
        fragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(container, fragment)
            .commit()
    }

    fun switchToSubFragment(container: Int, fragment: Fragment, tag: String, bundle: Bundle = Bundle.EMPTY) {
        fragment.arguments = bundle

        fragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }
}