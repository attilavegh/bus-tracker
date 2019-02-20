package hu.attilavegh.vbkoveto

import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import hu.attilavegh.vbkoveto.model.Bus
import hu.attilavegh.vbkoveto.utility.ActivityTitleUtils
import hu.attilavegh.vbkoveto.utility.FragmentUtils
import hu.attilavegh.vbkoveto.utility.ToastUtils

import kotlinx.android.synthetic.main.activity_driver.*

class DriverActivity : AppCompatActivity() {

    lateinit var titleController: ActivityTitleUtils
    private lateinit var toastController: ToastUtils
    private lateinit var fragmentController: FragmentUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)
        setSupportActionBar(toolbar)

        titleController = ActivityTitleUtils(toolbar)
        toastController = ToastUtils(this, resources)
        fragmentController = FragmentUtils(supportFragmentManager)
    }

    private fun driveBus(bus: Bus) {
        when (!bus.active) {
            true -> initDriveBusView(bus)
            false -> toastController.create(R.string.active_bus_message)
        }
    }

    private fun initDriveBusView(bus: Bus) {
        println("$bus driverMode")
    }
}
