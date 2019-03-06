package hu.attilavegh.vbkoveto.presenter.user

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.view.user.BusFragment.OnBusListItemInteractionListener
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.presenter.BusItemRecyclerViewAdapterBase
import hu.attilavegh.vbkoveto.presenter.BusViewHolderBase

import kotlinx.android.synthetic.main.fragment_bus_active.view.*

class BusItemRecyclerViewAdapter(
    private val listener: OnBusListItemInteractionListener?
) : BusItemRecyclerViewAdapterBase(listener) {

    private val onFavoriteClickListener: View.OnClickListener

    private val activeLayoutId: Int = R.layout.fragment_bus_active
    private val inactiveLayoutId: Int = R.layout.fragment_bus_inactive

    init {
        onFavoriteClickListener = View.OnClickListener { v ->
            val bus = v.tag as Bus
            addFavouriteListener(bus, v.fav_button)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (buses[position].active) {
            true -> activeLayoutId
            false -> inactiveLayoutId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolderBase {
        return when (viewType) {
            activeLayoutId -> ActiveUserViewHolder(parent)
            else -> InactiveUserViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: BusViewHolderBase, position: Int) {
        val bus = buses[position]
        holder.viewModel = bus

        with(holder.itemView) { setActionListener(this, bus, onBusItemClickListener) }
        with(holder.itemView.fav_button) { setActionListener(this, bus, onFavoriteClickListener) }
    }

    private fun addFavouriteListener(bus: Bus, button: ImageButton) {
        if (bus.favorite) {
            removeFavorite(bus, button)
        } else {
            addFavorite(bus, button)
        }
    }

    private fun removeFavorite(bus: Bus, button: ImageButton) {
        listener?.onFavoriteRemove(bus, button)
    }

    private fun addFavorite(bus: Bus, button: ImageButton) {
        listener?.onFavoriteAdd(bus, button)
    }
}
