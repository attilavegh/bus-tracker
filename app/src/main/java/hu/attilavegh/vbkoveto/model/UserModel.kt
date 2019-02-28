package hu.attilavegh.vbkoveto.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    val email: String = "",
    var isDriver: Boolean = false,
    val name: String = "",
    val imgUrl: String = ""): Parcelable