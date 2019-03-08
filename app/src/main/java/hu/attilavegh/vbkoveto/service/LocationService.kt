package hu.attilavegh.vbkoveto.service

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.android.gms.maps.model.LatLng
import hu.attilavegh.vbkoveto.R
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.json.JSONObject
import hu.attilavegh.vbkoveto.utility.ApplicationUtils
import org.json.JSONException

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

    fun getEstimatedTime(userPosition: LatLng, busPosition: LatLng): Observable<String> {
        val apiUrl ="https://maps.google.com/maps/api/directions/json?" +
                "origin=${userPosition.latitude},${userPosition.longitude}&" +
                "destination=${busPosition.latitude},${busPosition.longitude}&" +
                "units=metric&region=hu&" +
                "key=${activity.getString(R.string.maps_key)}"

        return Observable.create<String> { emitter ->
            apiUrl.httpGet().responseString { _, _, result ->
                when (result) {
                    is Result.Failure -> {
                        emitter.onError(result.getException())
                    }
                    is Result.Success -> {
                        emitter.onNext(getDuration(result.get()))
                    }
                }
            }
        }
    }

    fun getLocation(minTime: Long, minDistance: Float): Observable<Location> {
        requestLocationUpdates(minTime, minDistance)

        return location
    }

    fun pause() {
        locationManager.removeUpdates(locationListener)
    }

    fun resume(minTime: Long, minDistance: Float) {
        requestLocationUpdates(minTime, minDistance)
    }

    private fun getDuration(response: String): String {
        return try {
            val routes = JSONObject(response).getJSONArray("routes")
            val leg = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0)
            val estimatedTime = leg.getJSONObject("duration").getLong("value") * 1000

            val arrivalTime = estimatedTime + System.currentTimeMillis()

            ApplicationUtils.createDisplayTime(arrivalTime)
        } catch (exception: JSONException) {
            activity.getString(R.string.map_time_default)
        }
    }

    private fun requestLocationUpdates(minTime: Long, minDistance: Float) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener)
        }
    }
}