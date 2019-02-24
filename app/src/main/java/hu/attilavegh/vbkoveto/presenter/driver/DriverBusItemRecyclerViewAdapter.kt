package hu.attilavegh.vbkoveto.presenter.driver

import android.view.ViewGroup
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.presenter.BusItemRecyclerViewAdapterBase
import hu.attilavegh.vbkoveto.presenter.BusViewHolderBase
import hu.attilavegh.vbkoveto.view.driver.DriverBusFragment

class DriverBusItemRecyclerViewAdapter(
    listener: DriverBusFragment.OnDriverBusListItemInteractionListener?
): BusItemRecyclerViewAdapterBase(listener) {

    private val driverActiveLayoutId: Int = R.layout.fragment_driver_bus_active
    private val driverInactiveLayoutId: Int = R.layout.fragment_driver_bus_inactive

    override fun getItemViewType(position: Int): Int {
        return when (buses[position].active) {
            true -> driverInactiveLayoutId
            false -> driverActiveLayoutId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolderBase {
        return when (viewType) {
            driverActiveLayoutId -> ActiveDriverBusViewHolder(parent)
            else -> InactiveDriverBusViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: BusViewHolderBase, position: Int) {
        val bus = buses[position]
        holder.viewModel = bus

        with(holder.itemView) { setActionListener(this, bus, onBusItemClickListener) }
    }
}