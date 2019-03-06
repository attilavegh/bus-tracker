package hu.attilavegh.vbkoveto.presenter.driver

import android.view.ViewGroup
import android.widget.ImageButton
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.presenter.BusViewHolderBase

abstract class DriverViewHolder(
    parent: ViewGroup,
    layout: Int
): BusViewHolderBase(parent, layout) {

    private val resetStatusButton: ImageButton = itemView.findViewById(R.id.reset_status_button)

    protected fun tagResetStatusButton(bus: Bus) {
        with(resetStatusButton) { tag = bus }
    }
}