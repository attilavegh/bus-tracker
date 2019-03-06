package hu.attilavegh.vbkoveto.presenter.user

import android.view.ViewGroup
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.model.Bus

class InactiveUserViewHolder(
    parent: ViewGroup
): UserViewHolder(parent, R.layout.fragment_bus_inactive) {

    override var viewModel: Bus? = null
        set(value) {
            field = value
            value?.let { bus ->
                busName.text = bus.name

                setFavoriteStatus(bus)
                tagItem(bus)
                tagFavoriteButton(bus)
            }
        }
}