package hu.attilavegh.vbkoveto.view.driver

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.service.AuthenticationService
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.presenter.driver.DriverBusItemRecyclerViewAdapter
import hu.attilavegh.vbkoveto.service.FirebaseDataService
import hu.attilavegh.vbkoveto.utility.ErrorStatusUtils
import hu.attilavegh.vbkoveto.view.BusListItemInteractionListenerBase
import io.reactivex.disposables.Disposable

class DriverBusFragment: Fragment() {

    private var listener: OnDriverBusListItemInteractionListener? = null
    private lateinit var firebaseListener: Disposable

    private val firebaseDataService = FirebaseDataService()
    private lateinit var authenticationService: AuthenticationService
    private lateinit var errorStatusUtils: ErrorStatusUtils

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_driver_bus_list, container, false)
        authenticationService = AuthenticationService(context!!)
        errorStatusUtils = ErrorStatusUtils(activity!!)

        with(view as RecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = DriverBusItemRecyclerViewAdapter(listener)
        }

        firebaseListener = firebaseDataService.getBusList(context!!).subscribe({
            (view.adapter as DriverBusItemRecyclerViewAdapter).buses = it
        }, {
            errorStatusUtils.show(R.string.error, R.drawable.error)
        })

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
        listener = null
    }

    override fun onPause() {
        super.onPause()
        firebaseListener.dispose()
    }

    interface OnDriverBusListItemInteractionListener: BusListItemInteractionListenerBase {
        fun onResetStatus(bus: Bus, view: View)
    }

    companion object {
        fun newInstance(): DriverBusFragment = DriverBusFragment()
    }
}