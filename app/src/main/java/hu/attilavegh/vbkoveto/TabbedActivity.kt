package hu.attilavegh.vbkoveto

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import hu.attilavegh.vbkoveto.dummy.DummyContent
import hu.attilavegh.vbkoveto.fragment.BusFragment
import hu.attilavegh.vbkoveto.fragment.MapFragment
import hu.attilavegh.vbkoveto.fragment.ProfileFragment
import kotlinx.android.synthetic.main.activity_tabbed.*

class TabbedActivity: AppCompatActivity(),
    BusFragment.OnListFragmentInteractionListener,
    MapFragment.OnFragmentInteractionListener,
    ProfileFragment.OnFragmentInteractionListener {

    private lateinit var toolbar: Toolbar

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
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

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        toolbar.title = getString(R.string.title_buses)
        val busFragment = BusFragment.newInstance()
        openFragment(busFragment)
    }

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
    }

    override fun onFragmentInteraction(uri: Uri) {
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
