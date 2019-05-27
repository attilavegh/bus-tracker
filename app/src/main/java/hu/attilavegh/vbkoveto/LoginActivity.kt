package hu.attilavegh.vbkoveto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import hu.attilavegh.vbkoveto.service.AuthenticationService
import hu.attilavegh.vbkoveto.model.UserModel

import io.reactivex.disposables.Disposable
import hu.attilavegh.vbkoveto.utility.ApplicationUtils
import hu.attilavegh.vbkoveto.utility.ErrorStatusUtils
import hu.attilavegh.vbkoveto.utility.ProgressBarUtils

const val SIGN_IN_RESULT_CODE = 204

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var loginButton: Button

    private lateinit var errorStatusUtils: ErrorStatusUtils
    private lateinit var progressBar: ProgressBarUtils

    private lateinit var authenticationService: AuthenticationService
    private lateinit var authenticationListener: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        loginButton = findViewById(R.id.login_button)
        loginButton.setOnClickListener(this)

        authenticationService = AuthenticationService(this)
        errorStatusUtils = ErrorStatusUtils(this)
        progressBar = ProgressBarUtils(this)

        ApplicationUtils.createNotificationChannel(this)
        ApplicationUtils.checkPlayServices(this)

        initLogin()
    }

    private fun initLogin() {
        if (authenticationService.isLoggedIn()) {
            val user = authenticationService.getUser()
            loadApp(user)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.login_button -> onLogin()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN_RESULT_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun onLogin() {
        progressBar.show()
        startActivityForResult(authenticationService.createSignInIntent(), SIGN_IN_RESULT_CODE)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)!!

            authenticationListener = authenticationService.login(account).subscribe({
                loadApp(it)
            }, {
                progressBar.hide()
                errorStatusUtils.show(R.string.login_error, R.drawable.error)
            })
        } catch (e: ApiException) {
            progressBar.hide()

            when (e.statusCode) {
                7 -> errorStatusUtils.show(R.string.network_error, R.drawable.error)
                12501 -> errorStatusUtils.show(R.string.login_interrupted, R.drawable.error)
                else -> errorStatusUtils.show(R.string.login_error, R.drawable.error)
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
