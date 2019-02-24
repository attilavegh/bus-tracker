package hu.attilavegh.vbkoveto.presenter.driver

import android.view.ViewGroup
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.presenter.BusViewHolderBase
import hu.attilavegh.vbkoveto.presenter.user.BusViewHolder

class ActiveDriverBusViewHolder(
    parent: ViewGroup
): BusViewHolderBase(parent, R.layout.fragment_driver_bus_active) {

    override var viewModel: Bus? = null
        set(value) {
            field = value
            value?.let { bus ->
                busName.text = bus.name

                tagItem(bus)
            }
        }
}