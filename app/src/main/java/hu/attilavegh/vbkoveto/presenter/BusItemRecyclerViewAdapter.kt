package hu.attilavegh.vbkoveto.presenter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.view.BusFragment.OnListFragmentInteractionListener
import hu.attilavegh.vbkoveto.model.Bus

import kotlinx.android.synthetic.main.fragment_bus_active.view.*

class BusItemRecyclerViewAdapter(
    private val listener: OnListFragmentInteractionListener?
): RecyclerView.Adapter<BusViewHolder>() {

    private val onBusItemClickListener: View.OnClickListener
    private val onFavoriteClickListener: View.OnClickListener

    private val activeLayoutId: Int = R.layout.fragment_bus_active
    private val inactiveLayoutId: Int = R.layout.fragment_bus_inactive

    var buses: List<Bus> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        onBusItemClickListener = View.OnClickListener { v ->
            val bus = v.tag as Bus
            listener?.onBusSelection(bus)
        }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        return when (viewType) {
            activeLayoutId -> ActiveBusViewHolder(parent)
            else -> InactiveBusViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val bus = buses[position]
        holder.viewModel = bus

        with(holder.itemView) { setActionListener(this, bus, onBusItemClickListener) }
        with(holder.itemView.fav_button) { setActionListener(this, bus, onFavoriteClickListener) }
    }

    override fun getItemCount(): Int = buses.size


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

    private fun setActionListener(view: View, bus: Bus, listener: View.OnClickListener) {
        view.tag = bus
        view.setOnClickListener(listener)
    }
}
