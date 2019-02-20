package hu.attilavegh.vbkoveto

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ImageButton
import hu.attilavegh.vbkoveto.controller.NotificationController
import hu.attilavegh.vbkoveto.utilities.*

import hu.attilavegh.vbkoveto.model.*
import hu.attilavegh.vbkoveto.view.*
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity(),
    BusFragment.OnListFragmentInteractionListener,
    MapBusFragment.OnFragmentInteractionListener,
    MapBusesFragment.OnFragmentInteractionListener,
    ProfileFragment.OnFragmentInteractionListener,
    NotificationFragment.OnFragmentInteractionListener {

    private lateinit var toolbar: Toolbar

    lateinit var user: UserModel

    lateinit var titleUtils: ActivityTitleUtils
    private lateinit var toastUtils: ToastUtils
    private lateinit var fragmentUtils: FragmentUtils

    private lateinit var notificationController: NotificationController

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
        setContentView(R.layout.activity_user)
        toolbar = findViewById(R.id.toolbar)
        user = intent.getParcelableExtra("user")

        titleUtils = ActivityTitleUtils(toolbar)
        toastUtils = ToastUtils(this, resources)
        fragmentUtils = FragmentUtils(supportFragmentManager)

        notificationController = NotificationController(this)

        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.bus_list_item
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
        fragmentUtils.switchTo(fragment, bundle)
    }

    private fun onBusClick(bus: Bus) {
        checkBus(bus)
    }

    private fun checkBus(bus: Bus) {
        when (bus.active) {
            true -> initCheckBusView(bus)
            false -> toastUtils.create(R.string.inactive_bus_message)
        }
    }

    private fun initCheckBusView(bus: Bus) {
        val argument = Bundle()
        argument.putString("id", bus.id)

        val mapFragment = MapBusFragment.newInstance()
        fragmentUtils.switchTo(mapFragment, FragmentTagName.BUS_LOCATION.name, argument)

        titleUtils.set(bus.name)
    }
}
