package hu.attilavegh.vbkoveto.presenter

import android.view.ViewGroup
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.model.Bus

class InactiveBusViewHolder(
    parent: ViewGroup
): BusViewHolder(parent, R.layout.fragment_bus_inactive) {

    override var viewModel: Bus? = null
        set(value) {
            field = value
            value?.let { bus ->
                busName.text = bus.name

                setFavoriteStatus(bus)
                tagControls(bus)
            }
        }
}