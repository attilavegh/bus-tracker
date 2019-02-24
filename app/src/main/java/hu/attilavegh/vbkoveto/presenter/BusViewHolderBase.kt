package hu.attilavegh.vbkoveto.presenter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import hu.attilavegh.vbkoveto.model.Bus
import kotlinx.android.synthetic.main.fragment_bus_active.view.*

abstract class BusViewHolderBase(
    parent: ViewGroup,
    layout: Int
): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layout, parent, false)
) {
    abstract var viewModel: Bus?

    protected val busName: TextView = itemView.bus_name

    protected fun tagItem(bus: Bus) {
        with(itemView) { tag = bus }
    }
}