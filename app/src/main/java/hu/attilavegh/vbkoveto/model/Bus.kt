package hu.attilavegh.vbkoveto.model

data class Bus(val id: Number, val name: String, var isActive: Boolean, var favorite: Boolean, val departureTime: String = "") {
    override fun toString(): String = name
}

object MockBusData {
    val buses: MutableList<Bus> = ArrayList()

    init {
        buses.add(Bus(1, "Járat 1", true, false,"18:00"))
        buses.add(Bus(2, "Járat 2", true, false,"18:00"))
        buses.add(Bus(3, "Járat 3", true, false,"18:00"))
        buses.add(Bus(4, "Járat 4", false, false))
        buses.add(Bus(5, "Járat 5", false, false))
    }
}