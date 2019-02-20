package hu.attilavegh.vbkoveto.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import hu.attilavegh.vbkoveto.presenter.BusItemRecyclerViewAdapter
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.UserActivity
import hu.attilavegh.vbkoveto.service.FirebaseController

import hu.attilavegh.vbkoveto.model.Bus
import io.reactivex.disposables.Disposable

class BusFragment: Fragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var firebaseListener: Disposable

    private lateinit var parentActivity: UserActivity

    private val firebaseController = FirebaseController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bus_list, container, false)

        parentActivity = activity as UserActivity

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

        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        firebaseListener.dispose()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onBusSelection(bus: Bus)
        fun onFavoriteAdd(bus: Bus, button: ImageButton)
        fun onFavoriteRemove(bus: Bus, button: ImageButton)
    }

    companion object {
        fun newInstance(): BusFragment = BusFragment()
    }
}