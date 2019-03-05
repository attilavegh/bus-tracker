package hu.attilavegh.vbkoveto.model

data class DriverConfig(
    val email: String = "",
    val locationMinTime: Long = 15000,
    val locationMinDistance: Float = 400f
)