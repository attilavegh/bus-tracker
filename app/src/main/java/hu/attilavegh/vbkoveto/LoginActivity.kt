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
import hu.attilavegh.vbkoveto.service.FirebaseService
import hu.attilavegh.vbkoveto.model.UserModel

import io.reactivex.disposables.Disposable
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import hu.attilavegh.vbkoveto.utility.ApplicationUtils
import hu.attilavegh.vbkoveto.utility.ErrorStatusUtils
import hu.attilavegh.vbkoveto.utility.ProgressBarUtils

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var loginButton: Button

    private val firebaseController = FirebaseService()
    private lateinit var authController: AuthController
    private lateinit var firebaseListener: Disposable

    private lateinit var errorStatusUtils: ErrorStatusUtils
    private lateinit var progressBar: ProgressBarUtils

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        loginButton = findViewById(R.id.login_button)
        loginButton.setOnClickListener(this)

        authController = AuthController(this)
        errorStatusUtils = ErrorStatusUtils(this)
        progressBar = ProgressBarUtils(this)

        ApplicationUtils.createNotificationChannel(this)
        ApplicationUtils.checkPlayServices(this)

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
            .requestIdToken(getString(R.string.oauth_key))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun onLogin() {
        progressBar.show()

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
                    {
                        run {
                            progressBar.hide()
                            errorStatusUtils.show(R.string.loginError, R.drawable.error)
                        }
                    }
                )
            }
        } catch (e: ApiException) {
            progressBar.hide()

            when (e.statusCode) {
                7 -> errorStatusUtils.show(R.string.networkError, R.drawable.error)
                12501 -> errorStatusUtils.show(R.string.loginInterrupted, R.drawable.error)
                else -> errorStatusUtils.show(R.string.loginError, R.drawable.error)
            }
        }
    }

    private fun loadApp(user: UserModel) {
        val activityType = if (user.isDriver) DriverActivity::class.java else UserActivity::class.java
        val intent = Intent(this, activityType)

        this.startActivity(intent)
        finish()

        progressBar.hide()
    }
}
