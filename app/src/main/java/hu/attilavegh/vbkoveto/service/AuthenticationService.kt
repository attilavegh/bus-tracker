package hu.attilavegh.vbkoveto.service

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import hu.attilavegh.vbkoveto.model.UserModel
import com.google.gson.Gson
import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.model.DriverConfig
import io.reactivex.Observable

const val AUTH_SHARED_PREFERENCES_FILE_NAME = "hu.attilavegh.vbkoveto.controller.auth"
const val USER_PREFERENCES_NAME = "user"

class AuthenticationService(private val context: Context) {

    private val googleSignInClient: GoogleSignInClient

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDataService = FirebaseDataService()

    private var authSharedPreferences = context.getSharedPreferences(AUTH_SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE)
    private val gson = Gson()

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.resources.getString(R.string.oauth_key))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun login(account: GoogleSignInAccount): Observable<UserModel> {
        return firebaseAuthWithGoogle(account)
            .switchMap { firebaseDataService.getDriverConfig() }
            .map { driverConfig -> setUser(account, driverConfig) }
    }

    fun logout() {
        val preferencesEditor = authSharedPreferences.edit()
        preferencesEditor.remove(USER_PREFERENCES_NAME)
        preferencesEditor.apply()

        FirebaseAuth.getInstance().signOut()
    }

    fun getUser(): UserModel {
        if (isLoggedIn()) {
            val serializedUser = authSharedPreferences.getString(USER_PREFERENCES_NAME, "")
            return gson.fromJson<UserModel>(serializedUser, UserModel::class.java)
        }

        return UserModel()
    }

    fun isLoggedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)

        if (account != null && authSharedPreferences.contains(USER_PREFERENCES_NAME)) {
            return true
        }

        return false
    }

    fun createSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount): Observable<Boolean> {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        return Observable.create { emitter ->
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emitter.onNext(true)
                } else {
                    emitter.onError(task.exception!!)
                }
            }
        }
    }

    private fun setUser(account: GoogleSignInAccount, config: DriverConfig): UserModel {
        val isDriver = account.email == config.email
        val user = UserModel(account.email!!, isDriver, account.displayName!!, account.photoUrl.toString())

        val preferencesEditor = authSharedPreferences.edit()
        val serializedUser = gson.toJson(user)

        preferencesEditor.putString(USER_PREFERENCES_NAME, serializedUser)
        preferencesEditor.apply()

        return user
    }
}