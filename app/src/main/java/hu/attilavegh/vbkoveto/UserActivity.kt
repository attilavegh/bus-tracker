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

class UserActivity : AppCompatActivity(),
    BusFragment.OnBusListItemInteractionListener,
    MapBusFragment.OnBusFragmentInterActionListener,
    MapBusesFragment.OnBusesFragmentInteractionListener,
    ProfileFragment.OnProfileFragmentInteractionListener,
    NotificationFragment.OnNotificationFragmentInteractionListener {

    private lateinit var toolbar: Toolbar

    lateinit var user: UserModel

    lateinit var titleUtils: ActivityTitleUtils
    private lateinit var fragmentUtils: FragmentUtils
    private lateinit var notification: NotificationBarUtils
    private lateinit var vibratorUtils: VibratorUtils
    private lateinit var toastUtils: ToastUtils

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

            val bus = Bus(busId, busName)
            initCheckBusView(bus)
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
        toastUtils = ToastUtils(this)

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

    override fun onBusSelection(bus: Bus) {
        onBusClick(bus)
    }

    override fun onFavoriteAdd(bus: Bus, button: ImageButton) {
        if (notificationController.isEnabled()) {
            notificationController.add(bus)
            button.setImageResource(R.drawable.favorite_on)
        } else {
            toastUtils.create(R.string.notification_disabled)
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


    private fun openFragment(titleId: Int, fragment: Fragment, bundle: Bundle = Bundle.EMPTY) {
        titleUtils.set(getString(titleId))
        fragmentUtils.switchTo(R.id.user_fragment_container, fragment, bundle)
    }

    private fun onBusClick(bus: Bus) {
        when (bus.active) {
            true -> initCheckBusView(bus)
            false -> toastUtils.create(R.string.inactive_bus_message)
        }
    }

    private fun initCheckBusView(bus: Bus) {
        val argument = Bundle()
        argument.putString("id", bus.id)

        val mapFragment = MapBusFragment.newInstance()
        fragmentUtils.switchTo(R.id.user_fragment_container, mapFragment, FragmentTagName.BUS_LOCATION.name, argument)

        titleUtils.set(bus.name)
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
            val bus = Bus(busId, busName)
            initCheckBusView(bus)

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
}
