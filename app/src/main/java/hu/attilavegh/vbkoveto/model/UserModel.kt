package hu.attilavegh.vbkoveto.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    val email: String = "",
    var isDriver: Boolean = false,
    val name: String = "",
    val imgUrl: String = ""): Parcelable {

    fun getResizedImage(size: Int): String {
        if (imgUrl != "" && imgUrl != "null") {
            return imgUrl.replace("/s96", "/s$size")
        }

        return imgUrl
    }
}