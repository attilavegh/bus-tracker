package hu.attilavegh.vbkoveto

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.model.FragmentTagName
import hu.attilavegh.vbkoveto.utility.ActivityTitleUtils
import hu.attilavegh.vbkoveto.utility.FragmentUtils
import hu.attilavegh.vbkoveto.utility.ToastUtils
import hu.attilavegh.vbkoveto.view.driver.DriverMapFragment
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import hu.attilavegh.vbkoveto.controller.AuthController
import hu.attilavegh.vbkoveto.view.driver.DriverBusFragment

class DriverActivity : AppCompatActivity(),
    DriverBusFragment.OnDriverBusListItemInteractionListener,
    DriverMapFragment.OnMapDriverFragmentInteractionListener {

    private lateinit var toolbar: Toolbar

    private lateinit var titleUtils: ActivityTitleUtils
    private lateinit var toastUtils: ToastUtils
    private lateinit var fragmentUtils: FragmentUtils

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        setContentView(R.layout.activity_driver)
        toolbar = findViewById(R.id.driver_toolbar)
        setSupportActionBar(toolbar)

        createGoogleAuthClient()
        authController = AuthController(this)

        titleUtils = ActivityTitleUtils(toolbar)
        toastUtils = ToastUtils(this, resources)
        fragmentUtils = FragmentUtils(supportFragmentManager)

        fragmentUtils.switchTo(R.id.driver_container, DriverBusFragment.newInstance())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.driver_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.driver_logout) {
            googleSignInClient.signOut().addOnCompleteListener {
                authController.logout()

                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)

                finish()
            }

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBusSelection(bus: Bus) {
        driveBus(bus)
    }

    private fun driveBus(bus: Bus) {
        when (!bus.active) {
            true -> initDriveBusView(bus)
            false -> toastUtils.create(R.string.active_bus_message, 40)
        }
    }

    private fun initDriveBusView(bus: Bus) {
        val argument = Bundle()
        argument.putString("id", bus.id)

        val driverMapFragment = DriverMapFragment.newInstance()
        fragmentUtils.switchTo(R.id.driver_container, driverMapFragment, FragmentTagName.BUS_LOCATION.name, argument)

        titleUtils.set(bus.name)
    }

    private fun createGoogleAuthClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
}
