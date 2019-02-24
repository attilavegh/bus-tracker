package hu.attilavegh.vbkoveto.view.driver

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.controller.AuthController
import hu.attilavegh.vbkoveto.presenter.driver.DriverBusItemRecyclerViewAdapter
import hu.attilavegh.vbkoveto.service.FirebaseController
import hu.attilavegh.vbkoveto.view.BusListItemInteractionListenerBase
import io.reactivex.disposables.Disposable

class DriverBusFragment: Fragment() {

    private var listener: OnDriverBusListItemInteractionListener? = null
    private lateinit var firebaseListener: Disposable

    private val firebaseController = FirebaseController()
    private lateinit var authController: AuthController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_driver_bus_list, container, false)
        authController = AuthController(context!!)

        with(view as RecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = DriverBusItemRecyclerViewAdapter(listener)
        }

        firebaseListener = firebaseController.getBusList(context!!).subscribe {
            (view.adapter as DriverBusItemRecyclerViewAdapter).buses = it
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnDriverBusListItemInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnBusListItemInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        firebaseListener.dispose()
        listener = null
    }

    interface OnDriverBusListItemInteractionListener: BusListItemInteractionListenerBase

    companion object {
        fun newInstance(): DriverBusFragment = DriverBusFragment()
    }
}