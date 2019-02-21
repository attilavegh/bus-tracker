package hu.attilavegh.vbkoveto.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationModel(
    val busId: String = "",
    val busName: String = "",
    val title: String = ""
): Parcelable