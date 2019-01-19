package hu.attilavegh.vbkoveto.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView

import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.fragment.BusFragment.OnListFragmentInteractionListener
import hu.attilavegh.vbkoveto.model.Bus

import kotlinx.android.synthetic.main.fragment_bus.view.*

class BusItemRecyclerViewAdapter(
    private val buses: List<Bus>,
    private val listener: OnListFragmentInteractionListener?
): RecyclerView.Adapter<BusItemRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Bus
            listener?.onBusListInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_bus, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bus = buses.elementAt(position)
        val activeStatusText = holder.view.context.getString(R.string.bus_status_message)

        holder.busName.text = bus.name
        holder.busStatus.text = if (bus.isActive) activeStatusText else ""
        holder.busStatusTime.text = bus.departureTime

        if (!bus.isActive) {
            setInactiveBusItemStyle(holder)
        }

        with(holder.view) {
            tag = bus
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = buses.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val busName: TextView = view.bus_name
        val busStatus: TextView = view.bus_status
        val busDash: TextView = view.bus_status_time_dash
        val busStatusTime: TextView = view.bus_status_time
    }


    private fun setInactiveBusItemStyle(holder: ViewHolder) {
        holder.busName.layoutParams = createInactiveBusItemStyleParams()
        holder.busName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.lightGray))
        holder.busName.gravity = Gravity.CENTER_VERTICAL

        holder.busStatus.visibility = View.INVISIBLE
        holder.busDash.visibility = View.INVISIBLE
        holder.busStatusTime.visibility = View.INVISIBLE
    }

    private fun createInactiveBusItemStyleParams(): GridLayout.LayoutParams {
        val params = GridLayout.LayoutParams()
        params.height = GridLayout.LayoutParams.MATCH_PARENT
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 2)

        return params
    }
}
