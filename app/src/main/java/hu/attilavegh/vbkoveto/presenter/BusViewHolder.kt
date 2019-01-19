package hu.attilavegh.vbkoveto.presenter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.model.Bus
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_bus_active.view.*

abstract class BusViewHolder(
    parent: ViewGroup,
    layout: Int
): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layout, parent, false)
) {

    abstract var viewModel: Bus?

    protected val busName: TextView = itemView.bus_name
    protected val activeStatusText: String = itemView.context.getString(R.string.bus_status_message)
    
    private val favoriteButton: ImageButton = itemView.fav_button
    
    protected fun tagControls(bus: Bus) {
        with(itemView) { tag = bus }
        with(favoriteButton) { tag = bus }
    }
}