package hu.attilavegh.vbkoveto.model

data class DriverConfig(
    val email: String = "",
    val locationUpdateInterval: Int = 15000
)