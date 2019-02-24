package hu.attilavegh.vbkoveto.presenter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.controller.AuthController
import hu.attilavegh.vbkoveto.view.user.BusFragment.OnBusListItemInteractionListener
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.presenter.driver.ActiveDriverBusViewHolder
import hu.attilavegh.vbkoveto.presenter.driver.InactiveDriverBusViewHolder
import hu.attilavegh.vbkoveto.view.BusListItemInteractionListenerBase

import kotlinx.android.synthetic.main.fragment_bus_active.view.*

abstract class BusItemRecyclerViewAdapterBase(
    private val listener: BusListItemInteractionListenerBase?
): RecyclerView.Adapter<BusViewHolderBase>() {

    protected val onBusItemClickListener: View.OnClickListener

    var buses: List<Bus> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        onBusItemClickListener = View.OnClickListener { v ->
            val bus = v.tag as Bus
            listener?.onBusSelection(bus)
        }
    }

    override fun getItemCount(): Int = buses.size

    protected fun setActionListener(view: View, bus: Bus, listener: View.OnClickListener) {
        view.tag = bus
        view.setOnClickListener(listener)
    }
}
