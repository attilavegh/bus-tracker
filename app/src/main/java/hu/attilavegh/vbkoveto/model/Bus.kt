package hu.attilavegh.vbkoveto.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import hu.attilavegh.vbkoveto.utility.ApplicationUtils
import java.text.SimpleDateFormat
import java.util.*

data class Bus(
    val id: String = "",
    val name: String = "",
    val active: Boolean = false,
    var favorite: Boolean = false,
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val departureTime: Timestamp = Timestamp.now(),
    val notificationId: Int = 0
) {

    fun getFormattedDepartureTime(): String {
        return formatDepartureTime(departureTime)
    }

    private fun formatDepartureTime(timestamp: Timestamp): String {
        return ApplicationUtils.createDisplayTime(timestamp)
    }
}