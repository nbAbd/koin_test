package com.pieaksoft.event.consumer.android

import android.Manifest
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pieaksoft.event.consumer.android.ui.base.BaseActivity
import com.pieaksoft.event.consumer.android.utils.*
import android.content.IntentFilter
import android.bluetooth.BluetoothManager
import android.graphics.Point
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import com.inqbarna.tablefixheaders.TableFixHeaders
import com.pieaksoft.event.consumer.android.events.EventsVM
import com.pieaksoft.event.consumer.android.model.*
import com.pieaksoft.event.consumer.android.ui.events.EventRowAdapter
import com.pieaksoft.event.consumer.android.ui.events.EventsAdapter
import com.pieaksoft.event.consumer.android.ui.profile.ProfileVM
import com.pieaksoft.event.consumer.android.views.Dialogs
import com.pieaksoft.event.consumer.android.views.gant.MyGanttAdapter
import kotlinx.coroutines.launch
import nl.joery.timerangepicker.TimeRangePicker
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(R.layout.activity_main) {
    private lateinit var navController: NavController
    private var permissionDialog: Dialog? = null
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private var insertEvent: String = ""
    private var insertEventDate: Date? = null
    private var sliderPosition: Int = 0
    private val scanSettings by lazy {
        ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
            .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
            .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
            .setReportDelay(10L)
            .build()
    }

    private val eventsVm: EventsVM by viewModel()

    private val scanCallback by lazy {
        object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                val device = result?.getDevice()
                Log.e("test_log", "test device = " + device?.name)
                super.onScanResult(callbackType, result)
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                Log.e("test_log", "test results = " + results.toString())
                super.onBatchScanResults(results)
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("test_log", "test error code = " + errorCode)
                super.onScanFailed(errorCode)
            }
        }
    }

    // private val binding by viewBinding(ActivityMainBinding::bind)
    override fun setView() {
        // initChartView()
        eventsVm.getEventList()
        bluetoothAdapter = getBluetoothManager().adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        Log.e("test_log", "test = " + getBluetoothManager().adapter)
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothReceiver, filter)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)


        val bottomMenuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        val view = bottomMenuView.getChildAt(2)
        val itemView = view as BottomNavigationItemView

        val viewCustom =
            LayoutInflater.from(this).inflate(R.layout.custom_on_off_button, bottomMenuView, false)
        itemView.addView(viewCustom)

        val off = viewCustom.findViewById<AppCompatButton>(R.id.off_btn)
        val on = viewCustom.findViewById<AppCompatButton>(R.id.on_btn)

        on.setOnClickListener {
            if (!bluetoothAdapter.isEnabled) {
                resultBluetoothHandler.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            } else {
                startScanBluetooth()
            }
        }
        off.setOnClickListener {
            Toast.makeText(this, "This is test", Toast.LENGTH_SHORT).show()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {

                }
                R.id.coDriverFragment -> {

                }

                else -> {

                }

            }
        }

        Log.e("test_log","test = "+ sp.get(SHARED_PREFERENCES_CURRENT_USER_ID, ""))


        findViewById<AppCompatImageView>(R.id.menu).setOnClickListener {
            if (findViewById<ConstraintLayout>(R.id.menu_opened).visibility == View.GONE) {
                findViewById<ConstraintLayout>(R.id.menu_opened).show()
                findViewById<AppCompatImageView>(R.id.menu).setImageResource(R.drawable.ic_close2)
            } else {
                findViewById<ConstraintLayout>(R.id.menu_opened).hide()
                findViewById<AppCompatImageView>(R.id.menu).setImageResource(R.drawable.ic_menu)
            }
        }
        if (!sp.getBoolean(SHARED_PREFERENCES_NOTIFICATION_SETTINGS, true) ||
            !ExcuseMe.doWeHavePermissionFor(this, Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            showPermissionsDialog()
        }

        findViewById<AppCompatTextView>(R.id.log).setOnClickListener {
            findViewById<ConstraintLayout>(R.id.log_view).show()
        }

        findViewById<AppCompatImageView>(R.id.close_log).setOnClickListener {
            findViewById<ConstraintLayout>(R.id.log_view).hide()
        }

        findViewById<AppCompatButton>(R.id.insert_btn).setOnClickListener {
            Dialogs.showInsertEventDialog(this, object : Dialogs.EventInsertClick {
                override fun onEventClick(event: EventInsertCode) {
                    insertEvent = event.code
                    Dialogs.showDateTimeSelector(
                        this@MainActivity,
                        object : Dialogs.DateSelectListener {
                            override fun onDateSelect(date: Date) {
                                insertEventDate = date
                                var event = Event(
                                    null,
                                    EventInsertType.statusChange.type,
                                    insertEvent,
                                    date = date.formatToServerDateDefaults(),
                                    time = date.formatToServerTimeDefaults(),
                                    Location(-10.12345f, 48.23432f),
                                    shippingDocumentNumber = "test",
                                    totalEngineHours = 20,
                                    totalEngineMiles = 450,
                                    eventRecordOrigin = "AUTOMATICALLY_RECORDED_BY_ELD",
                                    eventRecordStatus = "ACTIVE",
                                    malfunctionIndicatorStatus = "NO_ACTIVE_MALFUNCTION",
                                    dataDiagnosticEventIndicatorStatus = "NO_ACTIVE_DATA_DIAGNOSTIC_EVENTS_FOR_DRIVER",
                                    driverLocationDescription = "chicago, IL",
                                    dutyStatus = "OFF_DUTY",
                                    certification = Certification("2021-10-11", "CERTIFIED")
                                )
                                eventsVm.insertEvent(event)

                            }
                        })
                }
            })
        }
    }

    override fun bindVM() {
        eventsVm.eventLiveData.observe(this, {
            Log.e("test_log", "test response = " + it)
        })

        eventsVm.eventListLiveData.observe(this, {
            Log.e("test_log", "test eventList response = " + it)
        })

        eventsVm.eventGroupByDateObservable.observe(this, {
            Log.e("test_log", "test eventList response Grpuop by = " + it)
            initChartView()
        })
    }


    private fun showPermissionsDialog() {
        permissionDialog = Dialog(this, R.style.ThemeDialog)
        permissionDialog?.setContentView(R.layout.dialog_permissions)
        val locationAllowTextView =
            permissionDialog?.findViewById<AppCompatTextView>(R.id.location_allow)
        val notificationAllowTextView =
            permissionDialog?.findViewById<AppCompatTextView>(R.id.notification_allow)
        if (sp.getBoolean(SHARED_PREFERENCES_NOTIFICATION_SETTINGS, true)) {
            notificationAllowTextView?.text = getString(R.string.allowed)
            notificationAllowTextView?.setTextColor(ContextCompat.getColor(this, R.color.white))
            notificationAllowTextView?.background?.colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(
                    ContextCompat.getColor(this, R.color.blue),
                    BlendModeCompat.SRC_IN
                )
        } else {
            notificationAllowTextView?.text = getString(R.string.allow)
            notificationAllowTextView?.setTextColor(ContextCompat.getColor(this, R.color.grey))
            notificationAllowTextView?.background?.colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(
                    ContextCompat.getColor(this, R.color.white),
                    BlendModeCompat.SRC_IN
                )
        }

        if (ExcuseMe.doWeHavePermissionFor(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationAllowTextView?.text = getString(R.string.allowed)
            locationAllowTextView?.setTextColor(ContextCompat.getColor(this, R.color.white))
            locationAllowTextView?.background?.colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(
                    ContextCompat.getColor(this, R.color.blue),
                    BlendModeCompat.SRC_IN
                )
        } else {
            locationAllowTextView?.text = getString(R.string.allow)
            locationAllowTextView?.setTextColor(ContextCompat.getColor(this, R.color.grey))
            locationAllowTextView?.background?.colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(
                    ContextCompat.getColor(this, R.color.white),
                    BlendModeCompat.SRC_IN
                )
        }


        locationAllowTextView?.setOnClickListener {
            ExcuseMe.couldYouGive(this).permissionFor(Manifest.permission.ACCESS_FINE_LOCATION) {
                locationAllowTextView.text = getString(R.string.allowed)
                locationAllowTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
                locationAllowTextView.background?.colorFilter = BlendModeColorFilterCompat
                    .createBlendModeColorFilterCompat(
                        ContextCompat.getColor(this, R.color.blue),
                        BlendModeCompat.SRC_IN
                    )
            }
        }
        permissionDialog?.show()
    }

    private fun startScanBluetooth() {
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.startScan(null, scanSettings, scanCallback);
            Log.d("test_log", "scan started");
        } else {
            Log.e("test_log", "could not get scanner object");
        }
    }

    private var resultBluetoothHandler =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> }

    private var bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            startScanBluetooth()
            if (intent.action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                when (state) {
                    BluetoothAdapter.STATE_ON -> {
                        startScanBluetooth()
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> {

                    }
                }
            }
        }
    }

    private fun initChartView() {
        val llm = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, true
        )
        findViewById<RecyclerView>(R.id.events_list).layoutManager = llm
        val eventsAdapter = EventsAdapter()
        findViewById<RecyclerView>(R.id.events_list).adapter = eventsAdapter
        eventsAdapter.list = eventsVm.getEventsGroupByDate()
        eventsAdapter.notifyDataSetChanged()

        findViewById<AppCompatTextView>(R.id.date_text).text =
            eventsAdapter.list.keys.elementAt(0)
                .getDateFromString().formatToServerDateDefaults2()

        findViewById<RecyclerView>(R.id.events_list).attachSnapHelperWithListener(
            PagerSnapHelper(),
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    sliderPosition = position
                    launch {
                        findViewById<AppCompatTextView>(R.id.date_text).text =
                            eventsAdapter.list.keys.elementAt(position)
                                .getDateFromString().formatToServerDateDefaults2()
                    }
                }

                override fun onSnapPositionDragging() {

                }

                override fun onSnapPositionNotChange(position: Int) {
                    launch {
                        findViewById<AppCompatTextView>(R.id.date_text).text =
                            eventsAdapter.list.keys.elementAt(position)
                                .getDateFromString().formatToServerDateDefaults2()
                    }
                }
            })

//        findViewById<AppCompatButton>(R.id.back_btn).setOnClickListener {
//            llm.scrollToPositionWithOffset(sliderPosition+1, eventsAdapter.list.size)
//            //findViewById<RecyclerView>(R.id.events_list).layoutManager.scrollToPositionWithOffset(sliderPosition + 1)
//            Log.e("test_log","test = scroll = "+ (sliderPosition - 1))
//        }
//        findViewById<AppCompatButton>(R.id.next_btn).setOnClickListener {
//            llm.scrollToPositionWithOffset(sliderPosition-1, eventsAdapter.list.size)
//           // Log.e("test_log","test = scroll = "+ (sliderPosition + 1))
//           // findViewById<RecyclerView>(R.id.events_list).layoutManager?.scrollToPosition(sliderPosition - 1)
//        }
    }


    private fun getBluetoothManager(): BluetoothManager {
        return Objects.requireNonNull(
            getSystemService(BLUETOOTH_SERVICE) as BluetoothManager,
            "cannot get BluetoothManager"
        )
    }

    override fun onDestroy() {
        unregisterReceiver(bluetoothReceiver)
        super.onDestroy()
    }

    companion object {
        fun newInstance(context: Context): Intent {
            return newIntent<MainActivity>(context).apply {

            }
        }
    }
}