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
import hu.attilavegh.vbkoveto.model.UserModel

class LoginActivity: AppCompatActivity(), View.OnClickListener {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById<Button>(R.id.login_button).setOnClickListener(this)

        createGoogleAuthClient()
        checkLoggedInAccount()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_button -> login()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 204) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun login() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 204)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            loadApp(account)
        } catch (e: ApiException) {
            // TODO: error handling
            println("Could not sign in:" + e.statusCode)
        }
    }

    private fun createGoogleAuthClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun checkLoggedInAccount() {
        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account != null) {
            loadApp(account)
        }
    }

    private fun loadApp(account: GoogleSignInAccount?) {
        val intent = Intent(this, TabbedActivity::class.java)

        if (account != null) {
            intent.putExtra("user", UserModel(account.email!!, account.displayName, account.photoUrl.toString()))
        }

        this.startActivity(intent)
        finish()
    }
}
