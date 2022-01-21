package com.pieaksoft.event.consumer.android.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isGone
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.ActivityMainBinding
import com.pieaksoft.event.consumer.android.databinding.CustomOnOffButtonBinding
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.*
import com.pieaksoft.event.consumer.android.network.ErrorHandler
import com.pieaksoft.event.consumer.android.ui.base.BaseActivityNew
import com.pieaksoft.event.consumer.android.ui.dialog.PermissionDialog
import com.pieaksoft.event.consumer.android.utils.*
import com.pieaksoft.event.consumer.android.utils.Storage.isNetworkEnable
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class MainActivity : BaseActivityNew<ActivityMainBinding>(ActivityMainBinding::inflate),
    IMainAction {
    private lateinit var navController: NavController
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }
    private val bluetoothLeScanner: BluetoothLeScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings by lazy {
        ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
            .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
            .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
            .setReportDelay(10L)
            .build()
    }

    private val eventViewModel: EventViewModel by viewModel()

    private val scanCallback by lazy {
        object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                val device = result?.device
                Log.e("test_log", "test device = ${device?.name}")
                super.onScanResult(callbackType, result)
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                Log.e("test_log", "test results = ${results.toString()}")
                super.onBatchScanResults(results)
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("test_log", "test error code = $errorCode")
                super.onScanFailed(errorCode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupBottomNavigationView()
        setCustomActionBar()
    }

    private fun setCustomActionBar() = with(binding.appBar) {
        setSupportActionBar(toolbar)
        menu.setOnClickListener {
            navController.navigate(R.id.action_show_menu)
        }

        log.setOnClickListener {
            navController.navigate(R.id.action_show_log)
        }

        dotInspect.setOnClickListener {
            if (eventViewModel.eventListRequiresCertification.value?.isNotEmpty() == true) {
                showCustomSnackBar("First certify events", type = SnackBarType.ERROR)
            } else {
                navController.navigate(R.id.action_show_dot_inspect)
            }
        }
    }

    override fun setupView() {
        eventViewModel.setEventsMock()

        LocalBroadcastManager.getInstance(this).apply {
            registerReceiver(driverSwapReceiver, IntentFilter(BROADCAST_SWAP_DRIVERS))
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothReceiver, filter)

        if (!ExcuseMe.doWeHavePermissionFor(
                this,
                *PermissionDialog.permissions.toTypedArray()
            ) || !NotificationManagerCompat.from(this).areNotificationsEnabled()
        ) {
            showPermissionsDialog()
        }

        binding.certificationNeedWarning.setOnClickListener {
            navController.navigate(R.id.action_show_records_certification)
        }
    }

    private fun setupBottomNavigationView() = with(binding) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        bottomNavigation.apply {
            setupWithNavController(navController = navHostFragment.navController)
            itemIconTintList = null
        }
        navController = navHostFragment.navController

        val bottomMenuView = bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val view = bottomMenuView.getChildAt(2)
        val itemView = view as BottomNavigationItemView

        val toggleGroup = CustomOnOffButtonBinding.inflate(
            LayoutInflater.from(this@MainActivity),
            bottomMenuView,
            false
        )
        itemView.addView(toggleGroup.root)

        toggleGroup.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            when (checkedId) {
                R.id.buttonOff -> turnOffBluetooth()
                R.id.buttonOn -> turnOnBluetooth()
            }
        }
    }

    private fun turnOffBluetooth() {
        if (bluetoothAdapter.isEnabled) {
            bluetoothAdapter.disable()
            toast("Bluetooth turned off")
        }
    }

    private fun turnOnBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
            toast("Bluetooth turned on")
        }
    }


    override fun onResume() {
        super.onResume()
        connectionStateMonitor.enable()
        if (!connectionStateMonitor.hasNetworkConnection()) onNegative()
        else onPositive()

        eventViewModel.getEventList()
    }

    override fun onPause() {
        connectionStateMonitor.disable()
        super.onPause()
    }

    override fun onPositive() {
        runOnUiThread {
            isNetworkEnable = true
            eventViewModel.checkNotSyncedEvents()
        }
    }

    override fun onNegative() {
        runOnUiThread {
            isNetworkEnable = false
        }
    }

    override fun bindViewModel() {
        eventViewModel.eventListRequiresCertification.observe(this, { events ->
            when (!events.isNullOrEmpty()) {
                true -> showCertificationNeedView()
                else -> hideCertificationNeedView()
            }
        })

        eventViewModel.progress.observe(this, {
            setProgressVisible(it)
        })

        eventViewModel.error.observe(this, {
            val error = ErrorHandler.getErrorMessage(it, this)
            Log.e("test_logerrror", "test insert error response = $error")
        })
    }

    private fun showPermissionsDialog() {
        PermissionDialog().apply { show(supportFragmentManager, PermissionDialog::class.java.name) }
    }

    private fun startScanBluetooth() {
        bluetoothLeScanner.startScan(null, scanSettings, scanCallback)
        Log.d("test_log", "scan started")
    }

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

    //  Пока бул нени кайтарышын билбейм, карап кором бирок
    private val driverSwapReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            eventViewModel.getEventList()
        }
    }

    override fun showActionBar() {
        if (supportActionBar?.isShowing == true) return
        supportActionBar?.show()
    }

    override fun hideActionBar() {
        if (supportActionBar?.isShowing == false) return
        supportActionBar?.hide()
    }

    override fun showBottomNavigation() {
        val navView = binding.bottomNavigation

        if (navView.isShown) return

        navView.also {
            it.show()
            it.animate()
                .translationY(0f)
                .setDuration(ANIMATION_DURATION)
                .setListener(null)
        }
    }

    override fun hideBottomNavigation() {
        val navView = binding.bottomNavigation

        if (navView.isGone) return

        binding.bottomNavigation.also {
            it.animate()
                .translationY(it.height.toFloat())
                .setDuration(ANIMATION_DURATION)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        it.isGone = true
                    }
                })
        }
    }

    override fun showCertificationNeedView() {
        if (binding.certificationNeedWarning.isShown) return

        binding.certificationNeedWarning.also {
            it.animate()
                .translationY(0f)
                .setDuration(ANIMATION_DURATION)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        it.show()
                    }
                })
        }
    }

    override fun hideCertificationNeedView() {
        if (binding.certificationNeedWarning.isGone) return

        binding.certificationNeedWarning.also {
            it.animate()
                .translationY(-it.height.toFloat())
                .setDuration(ANIMATION_DURATION)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        it.hide()
                    }
                })
        }
    }

    override fun onDestroy() {
        unregisterReceiver(bluetoothReceiver)
        super.onDestroy()
    }

    companion object {
        private const val ANIMATION_DURATION = 200L

        fun newInstance(context: Context): Intent {
            return newIntent<MainActivity>(context).apply {
            }
        }
    }
}