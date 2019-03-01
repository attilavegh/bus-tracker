package hu.attilavegh.vbkoveto.view.driver

import android.content.Context
import android.os.Bundle
import android.widget.Button
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
import android.support.v7.app.AlertDialog
import android.view.*
import hu.attilavegh.vbkoveto.DriverActivity
import io.reactivex.Observable

class DriverMapFragment: MapFragmentBase() {

    private lateinit var exitButton: Button
    private var hasExitedSuccessfully = false

    private var listener: OnMapDriverFragmentInteractionListener? = null
    private lateinit var statusListener: Disposable

    private lateinit var selectedBusId: String
    // private lateinit var currentPosition: LatLng
    private lateinit var marker: Marker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_driver, container, false)
        setHasOptionsMenu(true)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_driver) as SupportMapFragment
        mapFragment.getMapAsync(this)

        exitButton = view.findViewById(R.id.driver_map_exit)
        exitButton.setOnClickListener { showExitAlertDialog() }

        return view
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu!!.clear()
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
        statusListener = updateBusStatus(true).subscribe()
        getCurrentPosition()

        marker = map.addMarker(MarkerOptions().position(LatLng(21.0, 34.0)))
        updateBusLocation(selectedBusId)
    }

    private fun getSelectedBusId() {
        selectedBusId = arguments!!.getString("id") ?: ""
    }

    private fun updateBusLocation(id: String) {
        firebaseListener = firebaseController.updateBusLocation(id, GeoPoint(0.0, 0.0)).subscribe(
            { positionMarker(LatLng(0.0, 0.0)) },
            { errorStatusUtils.show(R.string.error, R.drawable.error) }
        )
    }

    private fun positionMarker(position: LatLng) {
        marker.remove()
        marker = map.addMarker(addCustomMarker().position(position))

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, CAMERA_ZOOM))
    }

    private fun getCurrentPosition() {

    }

    private fun showExitAlertDialog() {
        AlertDialog.Builder(context!!)
            .setTitle(R.string.endDriveTitle)
            .setMessage(R.string.endDriveQuestion)
            .setPositiveButton(R.string.endDrive) { _, _ -> exit()}
            .setNegativeButton(R.string.cancel_message, null).show()
    }

    private fun exit() {
        if (!hasExitedSuccessfully) {
            statusListener = updateBusStatus(false).subscribe ({
                hasExitedSuccessfully = true
                (activity!! as DriverActivity).titleUtils.setPrevious()
                fragmentManager!!.popBackStackImmediate()
            }, {
                errorStatusUtils.show(R.string.error, R.drawable.error)
            })
        }
    }

    private fun updateBusStatus(status: Boolean): Observable<String> {
        return firebaseController.updateBusStatus(selectedBusId, status).take(1)
    }
}
