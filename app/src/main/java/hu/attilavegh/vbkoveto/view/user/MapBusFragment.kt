package hu.attilavegh.vbkoveto.view.user

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
import hu.attilavegh.vbkoveto.view.CAMERA_ZOOM
import hu.attilavegh.vbkoveto.view.MapFragmentBase

class MapBusFragment : MapFragmentBase() {

    private var listener: OnBusFragmentInterActionListener? = null

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

        if (context is OnBusFragmentInterActionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnBusFragmentInterActionListener

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
            { toastUtils.create(R.string.error) }
        )
    }

    private fun onBusCheck(bus: Bus) {
        if (bus.active) {
            positionMarker(bus)
        } else {
            marker.remove()
            toastUtils.createLong(R.string.bus_became_inactive)
        }
    }

    private fun positionMarker(bus: Bus) {
        marker.remove()

        val position = LatLng(bus.location.latitude, bus.location.longitude)
        marker = map.addMarker(addCustomMarker().position(position))

        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(bus.location.latitude, bus.location.longitude),
                CAMERA_ZOOM
            )
        )
    }
}
