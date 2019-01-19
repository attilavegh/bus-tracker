package hu.attilavegh.vbkoveto.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hu.attilavegh.vbkoveto.R
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import hu.attilavegh.vbkoveto.controller.FirebaseController
import hu.attilavegh.vbkoveto.controller.ToastController
import hu.attilavegh.vbkoveto.model.Bus
import io.reactivex.disposables.Disposable

const val CAMERA_BOUND_PADDING = 200
const val CAMERA_ZOOM = 16.0f

class MapFragment : Fragment(), OnMapReadyCallback {

    private var listener: OnFragmentInteractionListener? = null

    private lateinit var map: GoogleMap
    private lateinit var selectedBusId: String
    private var buses: List<Bus> = listOf()

    private val firebaseController = FirebaseController()
    private lateinit var firebaseListener: Disposable
    private lateinit var toastController: ToastController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getSelectedBusId()
        initFirebaseListener()

        toastController = ToastController(context!!, resources)
        buses = listOf()

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
        firebaseListener.dispose()
        listener = null
    }

    interface OnFragmentInteractionListener

    companion object {
        fun newInstance(): MapFragment = MapFragment()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        when (buses.size) {
            0 -> onNoBus()
            1 -> onBusCheck()
            else -> onBusesCheck()
        }
    }

    private fun initFirebaseListener() {
        if (selectedBusId == "") {
            getBuses()
        } else {
            getBus(selectedBusId)
        }
    }

    private fun getBuses() {
        firebaseListener = firebaseController.getBusList().subscribe {
            buses = it

            if (!buses.isEmpty()) {
                onBusesCheck()
            } else {
                onNoBus()
            }
        }
    }

    private fun getBus(id: String) {
        firebaseListener = firebaseController.getBus(id).subscribe {
            buses = listOf(it)
            onBusCheck()
        }
    }

    private fun getSelectedBusId() {
        selectedBusId = arguments!!.getString("id") ?: ""
    }

    private fun onNoBus() {
        toastController.create(R.string.no_bus)
    }

    private fun onBusCheck() {
        val bus = buses[0]
        createMarker(bus)
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(bus.location.latitude, bus.location.longitude),
                CAMERA_ZOOM
            )
        )
    }

    private fun onBusesCheck() {
        val boundsBuilder = LatLngBounds.Builder()

        buses.filter { bus -> bus.active }.forEach {
            createMarker(it)
            boundsBuilder.include(LatLng(it.location.latitude, it.location.longitude))
        }

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), CAMERA_BOUND_PADDING))
    }

    private fun createMarker(bus: Bus) {
        val position = LatLng(bus.location.latitude, bus.location.longitude)
        map.addMarker(MarkerOptions().position(position))
    }
}
