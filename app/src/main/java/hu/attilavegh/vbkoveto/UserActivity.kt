package hu.attilavegh.vbkoveto

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.widget.ImageButton
import hu.attilavegh.vbkoveto.controller.AuthController
import hu.attilavegh.vbkoveto.utility.NotificationBarUtils
import hu.attilavegh.vbkoveto.controller.NotificationController
import hu.attilavegh.vbkoveto.utility.*

import hu.attilavegh.vbkoveto.model.*
import hu.attilavegh.vbkoveto.view.user.BusFragment
import hu.attilavegh.vbkoveto.view.user.MapBusFragment
import hu.attilavegh.vbkoveto.view.user.MapBusesFragment
import hu.attilavegh.vbkoveto.view.user.NotificationFragment
import hu.attilavegh.vbkoveto.view.user.ProfileFragment
import kotlinx.android.synthetic.main.activity_user.*
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import hu.attilavegh.vbkoveto.service.LocationService

class UserActivity : AppCompatActivity(),
    BusFragment.OnBusListItemInteractionListener,
    MapBusFragment.OnBusFragmentInterActionListener,
    MapBusesFragment.OnBusesFragmentInteractionListener,
    ProfileFragment.OnProfileFragmentInteractionListener,
    NotificationFragment.OnNotificationFragmentInteractionListener {

    private lateinit var toolbar: Toolbar

    private var selectedBus: Bus = Bus()
    lateinit var user: UserModel

    lateinit var titleUtils: ActivityTitleUtils
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var notification: NotificationBarUtils
    private lateinit var errorStatusUtils: ErrorStatusUtils
    private lateinit var vibratorUtils: VibratorUtils

    private lateinit var notificationController: NotificationController
    private lateinit var authController: AuthController

    private val inAppNotificationReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val busId = intent.getStringExtra("busId")
            val busName = intent.getStringExtra("busName")
            val message = intent.getStringExtra("title")

            val bus = Bus(busId, busName)
            showInAppNotification(message, bus)
        }
    }

    private val inAppNotificationHandler = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val busId = intent.getStringExtra("busId")
            val busName = intent.getStringExtra("busName")

            selectedBus = Bus(busId, busName)
            initBusView()
        }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.bus_list_item -> {
                openFragment(R.string.title_buses, BusFragment.newInstance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.map_item -> {
                openFragment(R.string.title_buses, MapBusesFragment.newInstance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.profile_item -> {
                openFragment(R.string.title_profile, ProfileFragment.newInstance())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        setContentView(R.layout.activity_user)
        toolbar = findViewById(R.id.toolbar)

        titleUtils = ActivityTitleUtils(toolbar)
        fragmentUtils = FragmentUtils(supportFragmentManager)
        vibratorUtils = VibratorUtils(this)
        notification = NotificationBarUtils(this)
        errorStatusUtils = ErrorStatusUtils(this)

        notificationController = NotificationController(this)
        authController = AuthController(this)

        enableNotification()
        initNotificationBroadcastManagers()
        user = authController.getUser()

        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.bus_list_item
    }

    override fun onResume() {
        super.onResume()

        notificationController.removeAllNotifications()
        checkBusFromNotification()
    }

    override fun onFavoriteAdd(bus: Bus, button: ImageButton) {
        if (notificationController.isEnabled()) {
            notificationController.add(bus)
            button.setImageResource(R.drawable.favorite_on)
        } else {
            errorStatusUtils.show(R.string.notification_disabled, R.drawable.notification_white)
        }
    }

    override fun onFavoriteRemove(bus: Bus, button: ImageButton) {
        notificationController.remove(bus)
        button.setImageResource(R.drawable.favorite_off)
    }

    override fun finishActivityAfterLogout() {
        finish()
    }

    override fun onBackPressed() {
        val busFragmentId: Int = R.id.bus_list_item
        val isMainFragmentActive = navigation.selectedItemId == busFragmentId

        val hasSubFragment = supportFragmentManager.backStackEntryCount > 0

        if (isMainFragmentActive || hasSubFragment) {
            super.onBackPressed()

            if (hasSubFragment) {
                titleUtils.setPrevious()
            }
        } else {
            navigation.selectedItemId = busFragmentId
            titleUtils.set(getString(R.string.title_buses))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LocationService.LOCATION_PERMISSION_REQUEST_CODE -> { loadBusView() }
            else -> {}
        }
    }

    override fun onBusSelection(bus: Bus) {
        selectedBus = bus

        when (bus.active) {
            true -> initBusView()
            false -> errorStatusUtils.show(R.string.inactive_bus_message, R.drawable.bus)
        }
    }

    private fun initBusView() {
        if (LocationService.checkPermission(this)) {
            loadBusView()
        } else {
            LocationService.requestPermission(this)
        }
    }

    private fun loadBusView() {
        val argument = Bundle()
        argument.putString("id", selectedBus.id)
        argument.putString("departure", selectedBus.getFormattedDepartureTime())
        argument.putDouble("latitude", selectedBus.location.latitude)
        argument.putDouble("longitude", selectedBus.location.longitude)

        val mapFragment = MapBusFragment.newInstance()
        fragmentUtils.switchTo(R.id.user_fragment_container, mapFragment, FragmentTagName.BUS_LOCATION.name, argument)

        titleUtils.set(selectedBus.name)
    }

    private fun enableNotification() {
        if (notificationController.isFirstStart()) {
            notificationController.markFirstStart()
            notificationController.enable()
        }
    }

    private fun checkBusFromNotification() {
        val isNotification = intent.getBooleanExtra("notification", false)
        val busId = intent.getStringExtra("busId")
        val busName = intent.getStringExtra("busName")

        if (isNotification) {
            selectedBus = Bus(busId, busName)
            initBusView()

            intent.removeExtra("notification")
        }
    }

    private fun showInAppNotification(message: String, bus: Bus) {
        if (navigation.selectedItemId != R.id.bus_list_item) {
            vibratorUtils.vibrate(100)
            notification.show(message, bus)
        } else {
            vibratorUtils.vibrate(100)
        }
    }

    private fun initNotificationBroadcastManagers() {
        LocalBroadcastManager.getInstance(this).registerReceiver(inAppNotificationReceiver, IntentFilter("inAppNotificationReceiver"))
        LocalBroadcastManager.getInstance(this).registerReceiver(inAppNotificationHandler, IntentFilter("inAppNotificationHandler"))
    }

    private fun openFragment(titleId: Int, fragment: Fragment, bundle: Bundle = Bundle.EMPTY) {
        titleUtils.set(getString(titleId))
        fragmentUtils.switchTo(R.id.user_fragment_container, fragment, bundle)
    }
}
