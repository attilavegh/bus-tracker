package hu.attilavegh.vbkoveto.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.controller.NotificationController
import android.support.v7.widget.SwitchCompat

class NotificationFragment: Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var notificationController: NotificationController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        notificationSwitch = view.findViewById(R.id.fav_switch) as SwitchCompat

        setNotificationStatus()
        setSwitchListener()

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        notificationController = NotificationController(context)

        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener

    companion object {
        fun newInstance(): NotificationFragment = NotificationFragment()
    }

    private fun setNotificationStatus() {
        notificationSwitch.isChecked = notificationController.isEnabled()
    }

    private fun setSwitchListener() {
        notificationSwitch.setOnCheckedChangeListener { _, value ->
            if (value) {
                notificationController.enable()
            } else {
                notificationController.disable()
            }
        }
    }
}
