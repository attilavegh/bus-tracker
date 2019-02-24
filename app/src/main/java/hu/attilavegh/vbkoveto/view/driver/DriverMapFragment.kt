package hu.attilavegh.vbkoveto.view.driver

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
import com.google.firebase.firestore.GeoPoint
import hu.attilavegh.vbkoveto.view.CAMERA_ZOOM
import hu.attilavegh.vbkoveto.view.MapFragmentBase
import io.reactivex.disposables.Disposable

class DriverMapFragment : MapFragmentBase() {

    private var listener: OnMapDriverFragmentInteractionListener? = null
    private lateinit var statusListener: Disposable

    private lateinit var selectedBusId: String
    // private lateinit var currentPosition: LatLng
    private lateinit var marker: Marker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_driver, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_driver) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnMapDriverFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()

        setBusStatus(false)
        listener = null
        statusListener.dispose()
    }

    interface OnMapDriverFragmentInteractionListener

    companion object {
        fun newInstance(): DriverMapFragment = DriverMapFragment()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        getSelectedBusId()
        setBusStatus(true)
        getCurrentPosition()

        marker = map.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)))
        updateBus(selectedBusId)
    }

    private fun getSelectedBusId() {
        selectedBusId = arguments!!.getString("id") ?: ""
    }

    private fun updateBus(id: String) {
        firebaseListener = firebaseController.updateBusLocation(id, GeoPoint(0.0, 0.0)).subscribe(
            { positionMarker(LatLng(0.0, 0.0)) },
            { toastUtils.create(R.string.busError) }
        )
    }

    private fun positionMarker(position: LatLng) {
        marker.remove()
        marker = map.addMarker(addCustomMarker().position(position))

        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                position,
                CAMERA_ZOOM
            )
        )
    }

    private fun setBusStatus(status: Boolean) {
        statusListener = firebaseController.updateBusStatus(selectedBusId, status).subscribe {}
    }

    private fun getCurrentPosition() {

    }
}
