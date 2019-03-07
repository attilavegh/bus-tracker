package hu.attilavegh.vbkoveto.view

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v4.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.service.FirebaseService
import hu.attilavegh.vbkoveto.utility.BitmapUtils
import hu.attilavegh.vbkoveto.utility.ErrorStatusUtils
import io.reactivex.disposables.CompositeDisposable

const val CAMERA_BOUND_PADDING = 300
const val CAMERA_ZOOM = 13.0f

open class MapFragmentBase : Fragment(), OnMapReadyCallback {

    protected val firebaseService = FirebaseService()
    protected val disposables = CompositeDisposable()

    protected lateinit var errorStatusUtils: ErrorStatusUtils
    protected lateinit var map: GoogleMap

    private lateinit var customMarker: BitmapDescriptor

    override fun onAttach(context: Context) {
        super.onAttach(context)
        errorStatusUtils = ErrorStatusUtils(activity!!)
    }

    override fun onDetach() {
        super.onDetach()
        disposables.dispose()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isMapToolbarEnabled = false
        customMarker = createCustomMarker()
    }

    protected fun addCustomMarker(): MarkerOptions {
        return MarkerOptions().icon(customMarker)
    }

    protected fun createCustomMarker(drawable: Int = R.drawable.marker): BitmapDescriptor {
        var markerBitmap = BitmapFactory.decodeResource(resources, drawable)
        markerBitmap = BitmapUtils.scaleBitmap(markerBitmap, 65, 90)

        return BitmapDescriptorFactory.fromBitmap(markerBitmap)
    }
}
