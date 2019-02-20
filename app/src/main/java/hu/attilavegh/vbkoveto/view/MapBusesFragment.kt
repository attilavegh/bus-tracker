package hu.attilavegh.vbkoveto.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hu.attilavegh.vbkoveto.R
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import hu.attilavegh.vbkoveto.model.Bus

class MapBusesFragment : MapFragmentBase() {

    private var listener: OnFragmentInteractionListener? = null

    private var markers: ArrayList<Marker> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_buses, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_buses) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    interface OnFragmentInteractionListener

    companion object {
        fun newInstance(): MapBusesFragment = MapBusesFragment()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        initFirebaseListener()
    }

    private fun initFirebaseListener() {
        getBuses()
    }

    private fun getBuses() {
        firebaseListener = firebaseController.getBusList(context!!).subscribe(
            { result -> onSuccess(result) },
            { error -> toastUtils.create(error.toString()) })
    }

    private fun onSuccess(result: List<Bus>) {
        val filteredBuses = result.filter { bus -> bus.active }

        if (!filteredBuses.isEmpty()) {
            onBusesCheck(filteredBuses)
        } else {
            removeMarkers()
            onNoBus()
        }
    }

    private fun onBusesCheck(buses: List<Bus>) {
        removeMarkers()

        if (!buses.isEmpty()) {
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(positionMarkers(buses), CAMERA_BOUND_PADDING))
        }
    }

    private fun positionMarkers(buses: List<Bus>): LatLngBounds {
        val boundsBuilder = LatLngBounds.builder()

        buses.forEach {
            val position = LatLng(it.location.latitude, it.location.longitude)
            boundsBuilder.include(position)
            markers.add(map.addMarker(MarkerOptions().position(position)))
        }

        return boundsBuilder.build()
    }

    private fun removeMarkers() {
        markers.forEach { it.remove() }
    }
}
