package hu.attilavegh.vbkoveto.service

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class LocationService(private val activity: Activity) {

    private var location = PublishSubject.create<Location>()

    private val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(newLocation: Location) {
            location.onNext(newLocation)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 9001

        fun checkPermission(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermission(activity: Activity) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    fun getLocation(minTime: Long, minDistance: Float): Observable<Location> {
        requestLocationUpdates(minTime, minDistance)

        return location
    }

    fun stop() {
        locationManager.removeUpdates(locationListener)
    }

    private fun requestLocationUpdates(minTime: Long, minDistance: Float) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener)
        }
    }
}