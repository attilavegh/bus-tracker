package hu.attilavegh.vbkoveto

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import hu.attilavegh.vbkoveto.controller.AuthController
import hu.attilavegh.vbkoveto.service.FirebaseController
import hu.attilavegh.vbkoveto.model.UserModel
import hu.attilavegh.vbkoveto.utility.PlayServicesUtils
import hu.attilavegh.vbkoveto.utility.ToastUtils

import io.reactivex.disposables.Disposable
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var authController: AuthController
    private val firebaseController = FirebaseController()
    private lateinit var firebaseListener: Disposable

    private val playServicesUtils = PlayServicesUtils(this)
    private lateinit var toastUtils: ToastUtils

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        findViewById<Button>(R.id.login_button).setOnClickListener(this)

        createNotificationChannel()

        authController = AuthController(this)
        toastUtils = ToastUtils(this, resources)

        playServicesUtils.checkPlayServices()

        initLogin()
    }

    private fun initLogin() {
        if (authController.isLoggedIn()) {
            val user = authController.getUser()
            loadApp(user)
        } else {
            createGoogleAuthClient()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.login_button -> onLogin()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 204) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun createGoogleAuthClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun onLogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 204)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)

            if (account != null) {
                firebaseListener = firebaseController.getDriverConfig().subscribe(
                    { result ->
                        run {
                            val user = authController.login(account, result)
                            loadApp(user)
                        }
                    },
                    { error -> toastUtils.create(R.string.loginError) }
                )
            }
        } catch (e: ApiException) {
            // TODO: error handling
            println("Could not sign in:" + e.statusCode)
        }
    }

    private fun loadApp(user: UserModel) {
        val activityType = if (user.isDriver) DriverActivity::class.java else UserActivity::class.java
        val intent = Intent(this, activityType)

        this.startActivity(intent)
        finish()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = getString(R.string.notification_channelId)
            val name = getString(R.string.notification_channelName)
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(id, name, importance)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
