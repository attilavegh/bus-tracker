package hu.attilavegh.vbkoveto.service

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import hu.attilavegh.vbkoveto.controller.AuthController
import hu.attilavegh.vbkoveto.controller.NotificationController
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.model.ContactConfig
import hu.attilavegh.vbkoveto.model.DriverConfig
import io.reactivex.Observable
import java.util.*

class FirebaseService {

    private var database: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getContactConfig(): Observable<ContactConfig> {
        return Observable.create { emitter ->

            database.collection("config").document("contactConfig").get()
                .addOnSuccessListener { config ->
                    if (config != null) {
                        emitter.onNext(config.toObject(ContactConfig::class.java)!!)
                    }
                }
                .addOnFailureListener { exception ->
                    emitter.onError(exception)
                    return@addOnFailureListener
                }
        }
    }

    fun getDriverConfig(): Observable<DriverConfig> {
        return Observable.create { emitter ->

            database.collection("config").document("driverConfig").get()
                .addOnSuccessListener { config ->
                    if (config != null) {
                        emitter.onNext(config.toObject(DriverConfig::class.java)!!)
                    }
                }
                .addOnFailureListener { exception ->
                    emitter.onError(exception)
                    return@addOnFailureListener
                }
        }
    }

    fun updateBusLocation(id: String, location: GeoPoint): Observable<String> {
        return Observable.create { emitter ->
            database.collection("buses").document(id)
                .update("location", location)
                .addOnCompleteListener { emitter.onNext(id) }
                .addOnFailureListener { e ->
                    emitter.onError(e)
                }
        }
    }

    fun updateBusStatus(id: String, active: Boolean, departureTime: Timestamp = Timestamp.now()): Observable<String> {
        return Observable.create { emitter ->
            database.collection("buses").document(id)
                .update("active", active, "departureTime", departureTime)
                .addOnCompleteListener { emitter.onNext(id) }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }

    fun getBusList(context: Context): Observable<List<Bus>> {
        val notificationController = NotificationController(context)
        val authController = AuthController(context)

        return Observable.create { emitter ->
            database.collection("buses")
                .addSnapshotListener(EventListener<QuerySnapshot> { busList, error ->
                    if (error != null) {
                        emitter.onError(error)
                        return@EventListener
                    }

                    val buses = ArrayList<Bus>()
                    for (bus in busList!!) {
                        if (bus != null) {
                            buses.add(bus.toObject(Bus::class.java))
                        }
                    }

                    val sortedBuses = sortBuses(buses, authController.getUser().isDriver)
                    setNotificationStatus(sortedBuses, notificationController)
                    emitter.onNext(sortedBuses)
                })
        }
    }

    fun getBus(id: String): Observable<Bus> {
        return Observable.create { emitter ->

            database.collection("buses").document(id)
                .addSnapshotListener(MetadataChanges.INCLUDE, EventListener<DocumentSnapshot> { bus, error ->

                    if (error != null) {
                        emitter.onError(error)
                        return@EventListener
                    }

                    if (bus != null && !bus.metadata.isFromCache) {
                        emitter.onNext(bus.toObject(Bus::class.java)!!)
                    }
                })
        }
    }

    private fun sortBuses(buses: List<Bus>, isDriver: Boolean): List<Bus> {
        return when (isDriver) {
            true -> buses.sortedWith(compareBy { it.active })
            false -> buses.sortedWith(compareBy { !it.active })
        }
    }

    private fun setNotificationStatus(buses: List<Bus>, controller: NotificationController) {
        buses.forEach { bus -> bus.favorite = controller.hasBus(bus.id) }
    }
}