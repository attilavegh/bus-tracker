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
import com.google.android.gms.maps.model.Marker
import hu.attilavegh.vbkoveto.model.Bus

class MapBusFragment : MapFragmentBase() {

    private var listener: OnFragmentInteractionListener? = null

    private lateinit var selectedBusId: String
    private lateinit var marker: Marker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_bus, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_bus) as SupportMapFragment
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener

    companion object {
        fun newInstance(): MapBusFragment = MapBusFragment()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        marker = map.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)))
        getSelectedBusId()
        getBus(selectedBusId)
    }

    private fun getSelectedBusId() {
        selectedBusId = arguments!!.getString("id") ?: ""
    }

    private fun getBus(id: String) {
        firebaseListener = firebaseController.getBus(id).subscribe(
            { onBusCheck(it) },
            { error -> toastUtils.create(error.toString()) }
        )
    }

    private fun onBusCheck(bus: Bus) {
        positionMarker(bus)
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(bus.location.latitude, bus.location.longitude),
                CAMERA_ZOOM
            )
        )
    }

    private fun positionMarker(bus: Bus) {
        val position = LatLng(bus.location.latitude, bus.location.longitude)
        marker.position = position
    }
}
