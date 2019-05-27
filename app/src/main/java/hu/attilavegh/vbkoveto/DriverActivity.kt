package hu.attilavegh.vbkoveto

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.model.FragmentTagName
import hu.attilavegh.vbkoveto.utility.ActivityTitleUtils
import hu.attilavegh.vbkoveto.utility.FragmentUtils
import hu.attilavegh.vbkoveto.view.driver.DriverMapFragment
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import hu.attilavegh.vbkoveto.service.AuthenticationService
import hu.attilavegh.vbkoveto.service.LocationService
import hu.attilavegh.vbkoveto.utility.ErrorStatusUtils
import hu.attilavegh.vbkoveto.view.driver.DriverBusFragment
import androidx.appcompat.widget.PopupMenu
import android.view.View
import hu.attilavegh.vbkoveto.service.FirebaseDataService
import io.reactivex.disposables.Disposable

class DriverActivity : AppCompatActivity(),
    DriverBusFragment.OnDriverBusListItemInteractionListener,
    DriverMapFragment.OnMapDriverFragmentInteractionListener {

    private var selectedBus: Bus = Bus()
    private lateinit var toolbar: Toolbar

    lateinit var titleUtils: ActivityTitleUtils
    private lateinit var errorStatusUtils: ErrorStatusUtils
    private lateinit var fragmentUtils: FragmentUtils

    private val firebaseDataService = FirebaseDataService()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authenticationService: AuthenticationService

    private lateinit var updateDisposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        setContentView(R.layout.activity_driver)
        toolbar = findViewById(R.id.driver_toolbar)
        setSupportActionBar(toolbar)

        createGoogleAuthClient()
        authenticationService = AuthenticationService(this)

        titleUtils = ActivityTitleUtils(toolbar)
        errorStatusUtils = ErrorStatusUtils(this)
        fragmentUtils = FragmentUtils(supportFragmentManager)

        titleUtils.set(getString(R.string.title_buses))
        fragmentUtils.switchToMainFragment(R.id.driver_fragment_container, DriverBusFragment.newInstance())
    }

    override fun onBackPressed() {
        val hasSubFragment = supportFragmentManager.backStackEntryCount > 0

        if (!hasSubFragment) {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.driver_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.driver_logout) {
            googleSignInClient.signOut().addOnCompleteListener {
                authenticationService.logout()

                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)

                finish()
            }

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResetStatus(bus: Bus, view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.driver_bus_item_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.driver_bus_item_menu -> firebaseDataService.updateBusStatus(bus.id, false).subscribe({}, {
                    errorStatusUtils.show(R.string.error, R.drawable.error)
                })
            }

            true
        }

        popupMenu.show()
    }

    override fun onBusSelection(bus: Bus) {
        selectedBus = bus

        when (!bus.active) {
            true -> initDriveBusView()
            false -> errorStatusUtils.show(R.string.active_bus_message, R.drawable.bus)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        val permissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

        when (requestCode) {
            LocationService.LOCATION_PERMISSION_REQUEST_CODE -> {
                if (permissionGranted) {
                    loadDriveBusView()
                } else {
                    errorStatusUtils.show(R.string.gps_needed, R.drawable.map)
                }
            }
            else -> {
            }
        }
    }

    private fun initDriveBusView() {
        if (LocationService.checkPermission(this)) {
            loadDriveBusView()
        } else {
            LocationService.requestPermission(this)
        }
    }

    private fun loadDriveBusView() {
        updateDisposable = firebaseDataService.updateBusStatus(selectedBus.id, true).subscribe({
            val argument = Bundle()
            argument.putString("id", selectedBus.id)
            titleUtils.set(selectedBus.name)

            val driverMapFragment = DriverMapFragment.newInstance()
            fragmentUtils.switchToSubFragment(
                R.id.driver_fragment_container,
                driverMapFragment,
                FragmentTagName.BUS_LOCATION.name,
                argument
            )
        }, {
            errorStatusUtils.show(R.string.bus_start_error, R.drawable.error)
        })
    }

    private fun createGoogleAuthClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
}
