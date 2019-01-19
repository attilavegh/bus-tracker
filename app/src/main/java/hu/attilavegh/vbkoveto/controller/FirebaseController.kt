package hu.attilavegh.vbkoveto.controller

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.model.RemoteConfig
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.*

const val LOG_TAG_CONFIG = "firebase_getConfig"
const val LOG_TAG_BUS_LIST = "firebase_getBusList"

class FirebaseController {
    private val source = Source.SERVER
    private var database: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getConfig(): Observable<RemoteConfig> {
        return Observable.create { emitter ->

            database.collection("config").document("data")
                .get(source)
                .addOnSuccessListener { config ->
                    if (config != null) {
                        emitter.onNext(config.toObject(RemoteConfig::class.java)!!)
                    } else {
                        Log.d(LOG_TAG_CONFIG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(LOG_TAG_CONFIG, "failed with ", exception)

                    emitter.onError(exception)
                    return@addOnFailureListener
                }
        }
    }

    fun getBusList(): Observable<List<Bus>> {
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

                    sortBuses(buses)
                    emitter.onNext(buses)
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

    private fun sortBuses(buses: List<Bus>) {
        when (true) {
            true -> buses.sortedWith(compareBy { !it.active })
            false -> buses.sortedWith(compareBy { it.active })
        }
    }
}