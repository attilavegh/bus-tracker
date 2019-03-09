package hu.attilavegh.vbkoveto.model

data class UserConfig(
    val locationMinTime: Long = 15000,
    val locationMinDistance: Float = 400f
)