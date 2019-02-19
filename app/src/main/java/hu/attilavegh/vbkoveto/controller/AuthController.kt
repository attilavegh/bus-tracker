package hu.attilavegh.vbkoveto.controller

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import hu.attilavegh.vbkoveto.model.UserModel
import com.google.gson.Gson
import hu.attilavegh.vbkoveto.model.DriverConfig

const val AUTH_SHARED_PREFERENCES_FILE_NAME = "hu.attilavegh.vbkoveto.controller.auth"
const val USER_PREFERENCES_NAME = "user"

class AuthController(val context: Context) {

    private var authSharedPreferences = context.getSharedPreferences(AUTH_SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE)
    private val gson = Gson()

    fun login(account: GoogleSignInAccount, config: DriverConfig): UserModel {
        val isDriver = account.email == config.email
        val user = UserModel(account.email!!, isDriver, account.displayName, account.photoUrl.toString())

        val preferencesEditor = authSharedPreferences.edit()
        val serializedUser = gson.toJson(user)

        preferencesEditor.putString(USER_PREFERENCES_NAME, serializedUser)
        preferencesEditor.apply()

        return user
    }

    fun logout() {
        val preferencesEditor = authSharedPreferences.edit()
        preferencesEditor.remove(USER_PREFERENCES_NAME)
        preferencesEditor.apply()
    }

    fun getUser(): UserModel {
        val serializedUser = authSharedPreferences.getString(USER_PREFERENCES_NAME, "")
        return gson.fromJson<UserModel>(serializedUser, UserModel::class.java)
    }

    fun isLoggedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)

        if (account != null && authSharedPreferences.contains(USER_PREFERENCES_NAME)) {
            return true
        }

        return false
    }
}