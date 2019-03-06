package hu.attilavegh.vbkoveto.presenter.driver

import android.view.View
import android.view.ViewGroup
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.presenter.BusItemRecyclerViewAdapterBase
import hu.attilavegh.vbkoveto.presenter.BusViewHolderBase
import hu.attilavegh.vbkoveto.view.driver.DriverBusFragment
import kotlinx.android.synthetic.main.fragment_driver_bus_active.view.*

class DriverBusItemRecyclerViewAdapter(
    private val listener: DriverBusFragment.OnDriverBusListItemInteractionListener?
): BusItemRecyclerViewAdapterBase(listener) {

    private val onResetStatusClickListener: View.OnClickListener

    private val driverActiveLayoutId: Int = R.layout.fragment_driver_bus_active
    private val driverInactiveLayoutId: Int = R.layout.fragment_driver_bus_inactive

    init {
        onResetStatusClickListener = View.OnClickListener { v ->
            val bus = v.tag as Bus
            addResetButtonListener(bus, v)
        }
    }

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
        with(holder.itemView.reset_status_button) { setActionListener(this, bus, onResetStatusClickListener)}
    }

    private fun addResetButtonListener(bus: Bus, view: View) {
        if (bus.active) {
            listener?.onResetStatus(bus, view)
        }
    }
}