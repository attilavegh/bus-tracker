package hu.attilavegh.vbkoveto.view

import android.content.Context
import android.support.v4.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.controller.FirebaseController
import hu.attilavegh.vbkoveto.controller.ToastController
import io.reactivex.disposables.Disposable

const val CAMERA_BOUND_PADDING = 300
const val CAMERA_ZOOM = 13.0f

open class MapFragmentBase : Fragment(), OnMapReadyCallback {

    protected lateinit var firebaseListener: Disposable

    protected val firebaseController = FirebaseController()
    protected lateinit var toastController: ToastController

    protected lateinit var map: GoogleMap


    override fun onAttach(context: Context) {
        super.onAttach(context)
        toastController = ToastController(context, resources)
    }

    override fun onDetach() {
        super.onDetach()
        firebaseListener.dispose()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    protected fun onNoBus() {
        toastController.create(R.string.no_bus)
    }
}
