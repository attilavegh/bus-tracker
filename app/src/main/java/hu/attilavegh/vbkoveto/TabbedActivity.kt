package hu.attilavegh.vbkoveto

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import hu.attilavegh.vbkoveto.model.Bus

import hu.attilavegh.vbkoveto.fragment.BusFragment
import hu.attilavegh.vbkoveto.fragment.MapFragment
import hu.attilavegh.vbkoveto.fragment.ProfileFragment
import hu.attilavegh.vbkoveto.model.UserModel

import kotlinx.android.synthetic.main.activity_tabbed.*

class TabbedActivity: AppCompatActivity(),
    BusFragment.OnListFragmentInteractionListener,
    MapFragment.OnFragmentInteractionListener,
    ProfileFragment.OnFragmentInteractionListener {

    private lateinit var toolbar: Toolbar

    lateinit var user: UserModel
    var isDriverMode: Boolean = false
    var driverEmail: String = "vattilaaa@gmail.com"

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.bus_list_item -> {
                toolbar.title = getString(R.string.title_buses)
                val busFragment = BusFragment.newInstance()
                openFragment(busFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.map_item -> {
                toolbar.title = getString(R.string.title_buses)
                val mapsFragment = MapFragment.newInstance()
                openFragment(mapsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.profile_item -> {
                toolbar.title = getString(R.string.title_profile)
                val profileFragment = ProfileFragment.newInstance()
                openFragment(profileFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed)
        toolbar = findViewById(R.id.toolbar)

        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.bus_list_item

        getUser()
        setApplicationMode()

        if (isDriverMode) {
            createDriverModeLayout()
        }
    }

    override fun onBusSelection(item: Bus) {
        onBusClick(item)
    }

    override fun onFavoriteAdd(item: Bus) {
        println(item.toString() + " add favorite")
    }

    override fun onFavoriteRemove(item: Bus) {
        println(item.toString() + " remove favorite")
    }

    override fun onMapInteraction() {
    }

    override fun logout() {
        finish()
    }

    override fun onBackPressed() {
        val busFragmentId: Int = R.id.bus_list_item

        if (navigation.selectedItemId == busFragmentId) {
            super.onBackPressed()
        } else {
            navigation.selectedItemId = busFragmentId
        }
    }

    private fun onBusClick(bus: Bus) {
        when (isDriverMode) {
            true -> driveBus(bus)
            false -> checkBusLocation(bus)
        }
    }

    private fun driveBus(bus: Bus) {
        if (!bus.isActive) {
            println(bus.toString() + " driverMode")
        }
    }

    private fun checkBusLocation(bus: Bus) {
        if (bus.isActive) {
            println(bus.toString() + " userMode")
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    private fun getUser() {
        user = intent.getSerializableExtra("user") as UserModel
    }

    private fun setApplicationMode() {
        isDriverMode = (user.email == driverEmail)
    }

    private fun createDriverModeLayout() {
        navigation.menu.getItem(1).isVisible = false
    }
}
