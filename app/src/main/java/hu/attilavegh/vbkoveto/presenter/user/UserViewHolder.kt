package hu.attilavegh.vbkoveto.presenter.user

import android.view.ViewGroup
import android.widget.ImageButton
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.controller.NotificationController
import hu.attilavegh.vbkoveto.presenter.BusViewHolderBase
import kotlinx.android.synthetic.main.fragment_bus_active.view.*

abstract class UserViewHolder(
    parent: ViewGroup,
    layout: Int
): BusViewHolderBase(parent, layout) {

    protected val activeStatusText: String = itemView.context.getString(R.string.bus_status_message)
    private val favoriteButton: ImageButton = itemView.fav_button

    private val notificationController = NotificationController(parent.context)

    protected fun setFavoriteStatus(bus: Bus) {
        if (notificationController.hasBus(bus.id)) {
            favoriteButton.setImageResource(R.drawable.favorite_on)
        } else {
            favoriteButton.setImageResource(R.drawable.favorite_off)
        }
    }

    protected fun tagFavoriteButton(bus: Bus) {
        with(favoriteButton) { tag = bus }
    }
}