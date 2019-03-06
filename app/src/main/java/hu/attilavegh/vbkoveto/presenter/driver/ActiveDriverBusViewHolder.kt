package hu.attilavegh.vbkoveto.presenter.driver

import android.view.ViewGroup
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.model.Bus

class ActiveDriverBusViewHolder(
    parent: ViewGroup
): DriverViewHolder(parent, R.layout.fragment_driver_bus_active) {

    override var viewModel: Bus? = null
        set(value) {
            field = value
            value?.let { bus ->
                busName.text = bus.name

                tagItem(bus)
                tagResetStatusButton(bus)
            }
        }
}