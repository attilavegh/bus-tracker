package hu.attilavegh.vbkoveto.model

import android.net.Uri
import java.io.Serializable

data class UserModel(val email: String, val name: String?, val imgUrl: String?): Serializable