package hu.attilavegh.vbkoveto.service

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import hu.attilavegh.vbkoveto.controller.NotificationController
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.model.ContactConfig
import hu.attilavegh.vbkoveto.model.DriverConfig
import hu.attilavegh.vbkoveto.model.UserConfig
import io.reactivex.Observable
import java.util.*

class FirebaseDataService {

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

    fun getUserConfig(): Observable<UserConfig> {
        return Observable.create { emitter ->
            database.collection("config").document("userConfig").get()
                .addOnSuccessListener { config ->
                    if (config != null) {
                        emitter.onNext(config.toObject(UserConfig::class.java)!!)
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
                .addOnSuccessListener {  emitter.onNext(id) }
                .addOnFailureListener { e ->
                    emitter.onError(e)
                }
        }
    }

    fun updateBusStatus(id: String, active: Boolean, departureTime: Timestamp = Timestamp.now()): Observable<String> {
        return Observable.create { emitter ->
            database.collection("buses").document(id)
                .update("active", active, "departureTime", departureTime)
                .addOnSuccessListener { emitter.onNext(id) }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }

    fun getBusList(context: Context): Observable<List<Bus>> {
        val notificationController = NotificationController(context)
        val isDriver = AuthenticationService(context).getUser().isDriver

        return Observable.create { emitter ->
            val busListSnapshotListenerRegistration = database.collection("buses")
                .addSnapshotListener(EventListener<QuerySnapshot> { busList, error ->
                    if (error != null) {
                        emitter.onError(error)
                        return@EventListener
                    }

                    val parsedBuses = ArrayList<Bus>()
                    for (bus in busList!!) {
                        val id = bus.id
                        val name = bus.data["name"] as String
                        val active = bus.data["active"] as Boolean
                        val favorite = notificationController.hasBus(bus.id)
                        val location = bus.data["location"] as GeoPoint
                        val departureTime = bus.data["departureTime"] as Timestamp

                        parsedBuses.add(Bus(id, name, active, favorite, location, departureTime))
                    }

                    val sortedBuses = sortBuses(parsedBuses, isDriver)
                    emitter.onNext(sortedBuses)
                })

            emitter.setCancellable { busListSnapshotListenerRegistration.remove() }
        }
    }

    fun getBus(id: String): Observable<Bus> {
        return Observable.create { emitter ->
            val busSnapshotListenerRegistration = database.collection("buses").document(id)
                .addSnapshotListener(MetadataChanges.INCLUDE, EventListener<DocumentSnapshot> { bus, error ->

                    if (error != null) {
                        emitter.onError(error)
                        return@EventListener
                    }

                    if (bus != null && !bus.metadata.isFromCache) {
                        emitter.onNext(bus.toObject(Bus::class.java)!!)
                    }

                })

            emitter.setCancellable { busSnapshotListenerRegistration.remove() }
        }
    }

    private fun sortBuses(buses: List<Bus>, isDriver: Boolean): List<Bus> {
        return when (isDriver) {
            true -> buses.sortedWith(compareBy { it.active })
            false -> buses.sortedWith(compareBy { !it.active })
        }
    }
}