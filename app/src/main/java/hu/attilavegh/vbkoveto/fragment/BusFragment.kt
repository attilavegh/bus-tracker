package hu.attilavegh.vbkoveto.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.attilavegh.vbkoveto.adapter.BusItemRecyclerViewAdapter
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.TabbedActivity

import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.model.MockBusData

class BusFragment: Fragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var parentActivity: TabbedActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bus_list, container, false)

        parentActivity = activity as TabbedActivity

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = BusItemRecyclerViewAdapter(MockBusData.buses, listener, parentActivity.isDriverMode)
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onBusSelection(item: Bus)
        fun onFavoriteAdd(item: Bus)
        fun onFavoriteRemove(item: Bus)
    }

    companion object {
        fun newInstance(): BusFragment = BusFragment()
    }
}