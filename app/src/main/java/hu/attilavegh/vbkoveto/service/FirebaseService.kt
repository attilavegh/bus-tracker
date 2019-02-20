package hu.attilavegh.vbkoveto.service

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import hu.attilavegh.vbkoveto.controller.NotificationController
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.model.ContactConfig
import hu.attilavegh.vbkoveto.model.DriverConfig
import io.reactivex.Observable
import java.util.*

const val LOG_TAG_DRIVER_CONFIG = "firebase_driverConfig"
const val LOG_TAG_CONTACT_CONFIG = "firebase_contactConfig"
const val LOG_TAG_BUS_LIST = "firebase_getBusList"
const val LOG_TAG_BUS_LOCATION_ERROR = "firebase_updateLocation"

class FirebaseController {
    private var database: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getContactConfig(): Observable<ContactConfig> {
        return Observable.create { emitter ->

            database.collection("config").document("contactConfig").get()
                .addOnSuccessListener { config ->
                    if (config != null) {
                        emitter.onNext(config.toObject(ContactConfig::class.java)!!)
                    } else {
                        Log.d(LOG_TAG_CONTACT_CONFIG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(LOG_TAG_CONTACT_CONFIG, "failed with ", exception)

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
                    } else {
                        Log.d(LOG_TAG_DRIVER_CONFIG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(LOG_TAG_DRIVER_CONFIG, "failed with ", exception)

                    emitter.onError(exception)
                    return@addOnFailureListener
                }
        }
    }

    fun updateBusLocation(bus: Bus, location: GeoPoint): Observable<Bus> {
        return Observable.create { emitter ->
            database.collection("buses").document(bus.id)
                .update("location", location)
                .addOnFailureListener { e ->
                    Log.d(LOG_TAG_BUS_LOCATION_ERROR, "failed with ", e)
                    emitter.onError(e)
                }
        }
    }

    fun getBusList(context: Context): Observable<List<Bus>> {
        val notificationController = NotificationController(context)

        return Observable.create { emitter ->
            database.collection("buses")
                .addSnapshotListener(EventListener<QuerySnapshot> { busList, error ->
                    if (error != null) {
                        Log.w(LOG_TAG_BUS_LIST, "Listen failed.", error)

                        emitter.onError(error)
                        return@EventListener
                    }

                    val buses = ArrayList<Bus>()
                    for (bus in busList!!) {
                        if (bus != null) {
                            buses.add(bus.toObject(Bus::class.java))
                        }
                    }

                    val sortedBuses = sortBuses(buses)
                    setNotificationStatus(sortedBuses, notificationController)
                    emitter.onNext(sortedBuses)
                })
        }
    }

    fun getBus(id: String): Observable<Bus> {
        return Observable.create { emitter ->

            database.collection("buses").document(id)
                .addSnapshotListener(EventListener<DocumentSnapshot> { bus, error ->
                    if (error != null) {
                        Log.w(LOG_TAG_BUS_LIST, "Listen failed.", error)

                        emitter.onError(error)
                        return@EventListener
                    }

                    if (bus != null) {
                        emitter.onNext(bus.toObject(Bus::class.java)!!)
                    }
                })
        }
    }

    private fun sortBuses(buses: List<Bus>): List<Bus> {
        return when (true) {
            true -> buses.sortedWith(compareBy { !it.active })
            false -> buses.sortedWith(compareBy { it.active })
        }
    }

    private fun setNotificationStatus(buses: List<Bus>, controller: NotificationController) {
        buses.forEach { bus -> bus.favorite = controller.hasBus(bus) }
    }
}