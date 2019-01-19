package hu.attilavegh.vbkoveto.model

data class RemoteConfig(
    val driverModeEmail: String,
    val businessEmail: String,
    val feedbackEmail: String,
    val website: String
) {
    constructor(): this("", "", "", "")
}