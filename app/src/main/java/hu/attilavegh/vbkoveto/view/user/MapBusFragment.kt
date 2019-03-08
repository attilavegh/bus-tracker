package hu.attilavegh.vbkoveto.view.user

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import hu.attilavegh.vbkoveto.R
import com.google.android.gms.maps.SupportMapFragment
import hu.attilavegh.vbkoveto.UserActivity
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.service.LocationService
import hu.attilavegh.vbkoveto.utility.NotificationBarUtils
import hu.attilavegh.vbkoveto.view.CAMERA_ZOOM
import hu.attilavegh.vbkoveto.view.MapFragmentBase
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo

class MapBusFragment : MapFragmentBase() {

    private var listener: OnBusFragmentInterActionListener? = null
    private lateinit var locationService: LocationService
    private lateinit var notificationBar: NotificationBarUtils

    private lateinit var departureLabel: TextView
    private lateinit var arrivalLabel: TextView

    private lateinit var selectedBusId: String
    private lateinit var initialPosition: LatLng

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_bus, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_bus) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationService = LocationService(activity as UserActivity)
        notificationBar = NotificationBarUtils(activity as UserActivity)

        selectedBusId = arguments!!.getString("id") ?: ""
        initialPosition = LatLng(arguments!!.getDouble("latitude"), arguments!!.getDouble("longitude"))

        departureLabel = view.findViewById(R.id.map_bus_departure)
        departureLabel.text = arguments!!.getString("departure") ?: "-"
        arrivalLabel = view.findViewById(R.id.map_bus_arrival)

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

        locationService.pause()
    }

    override fun onPause() {
        super.onPause()
        locationService.pause()
    }

    override fun onResume() {
        super.onResume()
        locationService.resume(15000, 100f)
    }

    interface OnBusFragmentInterActionListener

    companion object {
        fun newInstance(): MapBusFragment = MapBusFragment()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        positionMarker(initialPosition)

        firebaseService.getBus(selectedBusId)
            .doOnNext { onBusCheck(it) }
            .doOnError { errorStatusUtils.show(R.string.error, R.drawable.error) }
            .switchMap { bus ->
                locationService.getLocation(15000, 100f).switchMap { location ->
                    if (bus.active) {
                        getEstimatedArrival(bus, location)
                    } else {
                        Observable.create<String> { emitter -> emitter.onNext(getString(R.string.map_time_default)) }
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { arrivalLabel.text = it }
            .doOnError { arrivalLabel.text = "-" }
            .subscribe()
            .addTo(disposables)
    }

    private fun onBusCheck(bus: Bus) {
        if (bus.active) {
            departureLabel.text = bus.getFormattedDepartureTime()
            positionMarker(LatLng(bus.location.latitude, bus.location.longitude))
        } else {
            map.clear()
            departureLabel.setText(R.string.map_time_default)
            notificationBar.show(R.string.bus_became_inactive)
        }
    }

    private fun getEstimatedArrival(bus: Bus, userLocation: Location): Observable<String> {
        return locationService.getEstimatedTime(
            LatLng(bus.location.latitude, bus.location.longitude),
            LatLng(userLocation.latitude, userLocation.longitude)
        )
    }

    private fun positionMarker(position: LatLng) {
        map.clear()
        map.addMarker(addCustomMarker().position(position))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, CAMERA_ZOOM))
    }
}
