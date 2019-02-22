package hu.attilavegh.vbkoveto.model

data class NotificationModel (
    val id: Int = 0,
    val type: String = "",
    val busId: String = "",
    val busName: String = "",
    val title: String = ""
)