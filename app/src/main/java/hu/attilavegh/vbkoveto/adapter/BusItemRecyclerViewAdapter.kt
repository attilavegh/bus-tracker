package hu.attilavegh.vbkoveto.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView

import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.fragment.BusFragment.OnListFragmentInteractionListener
import hu.attilavegh.vbkoveto.model.Bus

import kotlinx.android.synthetic.main.fragment_bus.view.*

class BusItemRecyclerViewAdapter(
    private var buses: List<Bus>,
    private val listener: OnListFragmentInteractionListener?,
    private val isDriverMode: Boolean
): RecyclerView.Adapter<BusItemRecyclerViewAdapter.ViewHolder>() {

    private val onBusItemClickListener: View.OnClickListener
    private val onFavoriteClickListener: View.OnClickListener

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_bus, parent, false)
        sortBuses()

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bus = buses.elementAt(position)
        val activeStatusText = holder.view.context.getString(R.string.bus_status_message)

        holder.busName.text = bus.name
        holder.busStatus.text = if (bus.isActive) activeStatusText else ""
        holder.busStatusTime.text = bus.departureTime

        when (isDriverMode) {
            true -> {
                holder.favoriteButton.visibility = View.INVISIBLE

                if (bus.isActive) {
                    setDisabledBusItemStyle(holder)
                } else {
                    setDriverModeBusDetails(holder)
                }
            }

            false -> {
                if (!bus.isActive) {
                    setDisabledBusItemStyle(holder)
                }
            }
        }

        with(holder.view) {
            tag = bus
            setOnClickListener(onBusItemClickListener)
        }

        with(holder.favoriteButton) {
            tag = bus
            setOnClickListener(onFavoriteClickListener)
        }
    }

    override fun getItemCount(): Int = buses.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val busName: TextView = view.bus_name
        val busStatus: TextView = view.bus_status
        val busDash: TextView = view.bus_status_time_dash
        val busStatusTime: TextView = view.bus_status_time
        val favoriteButton: ImageButton = view.fav_button
    }

    private fun setDisabledBusItemStyle(holder: ViewHolder) {
        removeBusDetails(holder)
        setDisabledBusColor(holder)
    }

    private fun removeBusDetails(holder: ViewHolder) {
        holder.busName.layoutParams = createInactiveBusItemStyleParams()
        holder.busName.gravity = Gravity.CENTER_VERTICAL

        holder.busStatus.visibility = View.INVISIBLE
        holder.busDash.visibility = View.INVISIBLE
        holder.busStatusTime.visibility = View.INVISIBLE
    }

    private fun setDriverModeBusDetails(holder: ViewHolder) {
        holder.busDash.visibility = View.INVISIBLE
        holder.busStatusTime.visibility = View.INVISIBLE

        holder.busStatus.text = holder.view.context.getString(R.string.bus_status_message_driver_mode)
    }

    private fun setDisabledBusColor(holder: ViewHolder) {
        holder.busName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.lightGray))
    }

    private fun createInactiveBusItemStyleParams(): GridLayout.LayoutParams {
        val params = GridLayout.LayoutParams()
        params.height = GridLayout.LayoutParams.MATCH_PARENT
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 2)

        return params
    }

    private fun sortBuses() {
        buses = when (isDriverMode) {
            true -> buses.sortedWith(compareBy { it.isActive })
            false -> buses.sortedWith(compareBy { !it.isActive })
        }
    }

    private fun addFavouriteListener(bus: Bus, button: ImageButton) {
        if (bus.favorite) {
            addFavorite(bus, button)
        } else {
            removeFavorite(bus, button)
        }
    }

    private fun addFavorite(bus: Bus, button: ImageButton) {
        bus.favorite = false
        listener?.onFavoriteRemove(bus)
        button.setImageResource(R.drawable.favorite_off)
    }

    private fun removeFavorite(bus: Bus, button: ImageButton) {
        bus.favorite = true
        listener?.onFavoriteAdd(bus)
        button.setImageResource(R.drawable.favorite_on)
    }
}
