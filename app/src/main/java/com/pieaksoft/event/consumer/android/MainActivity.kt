package com.pieaksoft.event.consumer.android

import android.Manifest
import android.R.attr
import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.DialogCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pieaksoft.event.consumer.android.databinding.ActivityLoginBinding
import com.pieaksoft.event.consumer.android.databinding.ActivityMainBinding
import com.pieaksoft.event.consumer.android.ui.base.BaseActivity
import com.pieaksoft.event.consumer.android.utils.*
import androidx.core.app.ActivityCompat.startActivityForResult
import android.R.attr.action
import android.content.IntentFilter


class MainActivity : BaseActivity(R.layout.activity_main) {
    private lateinit var navController: NavController
    private var permissionDialog: Dialog? = null
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private val scanSettings by lazy {
        ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
            .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
            .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
            .setReportDelay(10L)
            .build()
    }

    private val scanCallback by lazy {
        object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                  val  device = result?.getDevice()
                  Log.e("test_log","test device = "+ device?.name)
                super.onScanResult(callbackType, result)
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                Log.e("test_log", "test results = " + results.toString())
                super.onBatchScanResults(results)
            }
        }
    }

    // private val binding by viewBinding(ActivityMainBinding::bind)
    override fun setView() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

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
    }

    override fun bindVM() {

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

    private fun startScanBluetooth(){
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