package hu.attilavegh.vbkoveto.presenter.user

import android.view.ViewGroup
import android.widget.TextView
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.model.Bus
import kotlinx.android.synthetic.main.fragment_bus_active.view.*

class ActiveUserViewHolder(
    parent: ViewGroup
): UserViewHolder(parent, R.layout.fragment_bus_active) {

    private val busStatus: TextView = itemView.bus_status
    private val busStatusTime: TextView = itemView.bus_status_time

    override var viewModel: Bus? = null
        set(value) {
            field = value
            value?.let { bus ->
                busName.text = bus.name
                busStatus.text = if (bus.active) activeStatusText else ""
                busStatusTime.text = bus.getFormattedTimestamp()

                setFavoriteStatus(bus)
                tagItem(bus)
                tagFavoriteButton(bus)
            }
        }
}