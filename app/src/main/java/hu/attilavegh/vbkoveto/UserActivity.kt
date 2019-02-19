package hu.attilavegh.vbkoveto

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import hu.attilavegh.vbkoveto.utilities.*

import hu.attilavegh.vbkoveto.model.*
import hu.attilavegh.vbkoveto.view.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity(),
    BusFragment.OnListFragmentInteractionListener,
    MapBusFragment.OnFragmentInteractionListener,
    MapBusesFragment.OnFragmentInteractionListener,
    ProfileFragment.OnFragmentInteractionListener,
    NotificationFragment.OnFragmentInteractionListener {

    private lateinit var toolbar: Toolbar

    lateinit var user: UserModel

    lateinit var titleController: ActivityTitleUtils
    private lateinit var toastController: ToastUtils
    private lateinit var fragmentController: FragmentUtils


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

        initControllers()

        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.bus_list_item
    }

    override fun onBusSelection(bus: Bus) {
        onBusClick(bus)
    }

    override fun onFavoriteAdd(bus: Bus) {
        println("$bus add favorite")
    }

    override fun onFavoriteRemove(bus: Bus) {
        println("$bus remove favorite")
    }

    override fun onNotificationInteraction() {
        // TODO: handle notifications
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
                titleController.setPrevious()
            }
        } else {
            navigation.selectedItemId = busFragmentId
            titleController.set(getString(R.string.title_buses))
        }
    }


    private fun openFragment(titleId: Int, fragment: Fragment, bundle: Bundle = Bundle.EMPTY) {
        titleController.set(getString(titleId))
        fragmentController.switchTo(fragment, bundle)
    }

    private fun initControllers() {
        titleController = ActivityTitleUtils(toolbar)
        toastController = ToastUtils(this, resources)
        fragmentController = FragmentUtils(supportFragmentManager)
    }

    private fun onBusClick(bus: Bus) {
        checkBus(bus)
    }

    private fun checkBus(bus: Bus) {
        when (bus.active) {
            true -> initCheckBusView(bus)
            false -> toastController.create(R.string.inactive_bus_message)
        }
    }

    private fun initCheckBusView(bus: Bus) {
        val argument = Bundle()
        argument.putString("id", bus.id)

        val mapFragment = MapBusFragment.newInstance()
        fragmentController.switchTo(mapFragment, FragmentTagName.BUS_LOCATION.name, argument)

        titleController.set(bus.name)
    }
}
