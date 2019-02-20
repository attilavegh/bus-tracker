package hu.attilavegh.vbkoveto.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.text.SimpleDateFormat
import java.util.*

data class Bus(
    val id: String = "",
    val name: String = "",
    val active: Boolean = false,
    var favorite: Boolean = false,
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val departureTime: Timestamp = Timestamp.now()
) {

    fun getFormattedTimestamp(): String {
        return formatDepartureTime(departureTime)
    }

    private fun formatDepartureTime(timestamp: Timestamp): String {
        return if (timestamp != null) {
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.GERMANY)
            simpleDateFormat.format(timestamp.toDate())
        } else {
            ""
        }
    }
}