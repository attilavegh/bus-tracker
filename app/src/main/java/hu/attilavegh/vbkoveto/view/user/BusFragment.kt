package hu.attilavegh.vbkoveto.view.user

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import hu.attilavegh.vbkoveto.presenter.user.BusItemRecyclerViewAdapter
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.controller.AuthController
import hu.attilavegh.vbkoveto.service.FirebaseController

import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.view.BusListItemInteractionListenerBase
import io.reactivex.disposables.Disposable

class BusFragment: Fragment() {

    private var listener: OnBusListItemInteractionListener? = null
    private lateinit var firebaseListener: Disposable

    private val firebaseController = FirebaseController()
    private lateinit var authController: AuthController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bus_list, container, false)
        authController = AuthController(context!!)

        with(view as RecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = BusItemRecyclerViewAdapter(listener)
        }

        firebaseListener = firebaseController.getBusList(context!!).subscribe {
            (view.adapter as BusItemRecyclerViewAdapter).buses = it
        }

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
        firebaseListener.dispose()
        listener = null
    }

    interface OnBusListItemInteractionListener: BusListItemInteractionListenerBase {
        fun onFavoriteAdd(bus: Bus, button: ImageButton)
        fun onFavoriteRemove(bus: Bus, button: ImageButton)
    }

    companion object {
        fun newInstance(): BusFragment = BusFragment()
    }
}