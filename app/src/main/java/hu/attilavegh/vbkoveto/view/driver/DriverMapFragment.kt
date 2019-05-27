package hu.attilavegh.vbkoveto.view.driver

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import hu.attilavegh.vbkoveto.R
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.GeoPoint
import hu.attilavegh.vbkoveto.view.MapFragmentBase
import androidx.appcompat.app.AlertDialog
import android.view.*
import android.widget.TextView
import hu.attilavegh.vbkoveto.DriverActivity
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.model.DriverConfig
import hu.attilavegh.vbkoveto.service.LocationService
import hu.attilavegh.vbkoveto.utility.StopwatchUtils
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo

class DriverMapFragment : MapFragmentBase() {

    private var listener: OnMapDriverFragmentInteractionListener? = null

    private lateinit var driverActivity: DriverActivity
    private lateinit var locationService: LocationService
    private lateinit var stopwatchUtils: StopwatchUtils

    private lateinit var selectedBusId: String
    private lateinit var exitButton: Button
    private lateinit var counterView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_driver, container, false)
        setHasOptionsMenu(true)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_driver) as SupportMapFragment
        mapFragment.getMapAsync(this)

        selectedBusId = arguments!!.getString("id") ?: ""
        driverActivity = activity as DriverActivity

        locationService = LocationService(driverActivity)

        exitButton = view.findViewById(R.id.driver_map_exit)
        exitButton.setOnClickListener { showExitAlertDialog() }

        counterView = view.findViewById(R.id.driver_time_counter)
        stopwatchUtils = StopwatchUtils(counterView)

        return view
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
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

        stopwatchUtils.stop()
        locationService.pause()
    }

    interface OnMapDriverFragmentInteractionListener

    companion object {
        fun newInstance(): DriverMapFragment = DriverMapFragment()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        stopwatchUtils.start()

        firebaseDataService.getBus(selectedBusId).subscribe({
            checkBusStatus(it)
        }, {
            errorStatusUtils.show(R.string.error, R.drawable.error)
        }).addTo(disposables)

        firebaseDataService.getDriverConfig()
            .switchMap { locationService.getLocation(it.locationMinTime, it.locationMinDistance) }
            .map { location: Location -> LatLng(location.latitude, location.longitude) }
            .switchMap { position: LatLng -> updateBusLocation(position) }
            .subscribe({}, {
                errorStatusUtils.show(R.string.error, R.drawable.error)
            }).addTo(disposables)
    }

    private fun updateBusLocation(position: LatLng): Observable<String> {
        positionMarker(position)

        val firebaseLocation = GeoPoint(position.latitude, position.longitude)
        return firebaseDataService.updateBusLocation(selectedBusId, firebaseLocation)
            .doOnNext { positionMarker(position) }
    }

    private fun positionMarker(position: LatLng) {
        map.clear()
        map.addMarker(addCustomMarker().position(position))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, cameraZoom))
    }

    private fun showExitAlertDialog() {
        AlertDialog.Builder(context!!)
            .setTitle(R.string.end_drive_title)
            .setMessage(R.string.end_drive_question)
            .setPositiveButton(R.string.end_drive) { _, _ -> exit() }
            .setNegativeButton(R.string.cancel_message, null).show()
    }

    private fun exit() {
        firebaseDataService.updateBusStatus(selectedBusId, false).subscribe({
            fragmentManager!!.popBackStackImmediate()
            driverActivity.titleUtils.setPrevious()
        }, {
            errorStatusUtils.show(R.string.error, R.drawable.error)
        }).addTo(disposables)
    }

    private fun checkBusStatus(bus: Bus) {
        if (!bus.active) {
            exit()
        }
    }
}
