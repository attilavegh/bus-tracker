package hu.attilavegh.vbkoveto.presenter

import androidx.recyclerview.widget.RecyclerView
import android.view.View

import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.view.BusListItemInteractionListenerBase

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
