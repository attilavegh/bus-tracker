package hu.attilavegh.vbkoveto.view

import android.content.Context
import android.graphics.BitmapFactory
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.service.FirebaseDataService
import hu.attilavegh.vbkoveto.utility.BitmapUtils
import hu.attilavegh.vbkoveto.utility.ErrorStatusUtils
import io.reactivex.disposables.CompositeDisposable

open class MapFragmentBase : Fragment(), OnMapReadyCallback {

    protected val cameraBoundPadding = 300
    protected val cameraZoom = 15.0f

    protected val firebaseDataService = FirebaseDataService()
    protected val disposables = CompositeDisposable()

    protected lateinit var errorStatusUtils: ErrorStatusUtils
    protected lateinit var map: GoogleMap

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
    }

    protected fun addCustomMarker(drawable: Int = R.drawable.marker): MarkerOptions {
        return MarkerOptions().icon(createCustomMarker(drawable))
    }

    protected fun createCustomMarker(drawable: Int = R.drawable.marker): BitmapDescriptor {
        var markerBitmap = BitmapFactory.decodeResource(resources, drawable)
        markerBitmap = BitmapUtils.scaleBitmap(markerBitmap, 65, 90)

        return BitmapDescriptorFactory.fromBitmap(markerBitmap)
    }
}
