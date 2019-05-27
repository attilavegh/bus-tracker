package hu.attilavegh.vbkoveto.view.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import hu.attilavegh.vbkoveto.presenter.user.BusItemRecyclerViewAdapter
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.service.AuthenticationService
import hu.attilavegh.vbkoveto.service.FirebaseDataService

import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.utility.ErrorStatusUtils
import hu.attilavegh.vbkoveto.utility.NotificationBarUtils
import hu.attilavegh.vbkoveto.view.BusListItemInteractionListenerBase
import io.reactivex.disposables.Disposable

class BusFragment: Fragment() {

    private var listener: OnBusListItemInteractionListener? = null
    private lateinit var firebaseListener: Disposable

    private val firebaseDataService = FirebaseDataService()
    private lateinit var authenticationService: AuthenticationService
    private lateinit var errorStatusUtils: ErrorStatusUtils


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bus_list, container, false)
        authenticationService = AuthenticationService(context!!)
        errorStatusUtils = ErrorStatusUtils(activity!!)

        with(view as RecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = BusItemRecyclerViewAdapter(listener)
        }

        firebaseListener = firebaseDataService.getBusList(context!!).subscribe({
            (view.adapter as BusItemRecyclerViewAdapter).buses = it
        }, {
            errorStatusUtils.show(R.string.buses_load_error, R.drawable.error)
        })

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnBusListItemInteractionListener) {
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

    interface OnBusListItemInteractionListener: BusListItemInteractionListenerBase {
        fun onFavoriteAdd(bus: Bus, button: ImageButton)
        fun onFavoriteRemove(bus: Bus, button: ImageButton)
    }

    companion object {
        fun newInstance(): BusFragment = BusFragment()
    }
}