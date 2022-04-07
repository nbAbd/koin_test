package com.pieaksoft.event.consumer.android.ui.activities.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.araujo.jordan.excuseme.ExcuseMe
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.ActivityMainBinding
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.network.ErrorHandler
import com.pieaksoft.event.consumer.android.ui.base.BaseActivityNew
import com.pieaksoft.event.consumer.android.ui.dialog.PermissionDialog
import com.pieaksoft.event.consumer.android.utils.*
import com.pieaksoft.event.consumer.android.utils.Storage.isNetworkEnable
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivityNew<ActivityMainBinding>(ActivityMainBinding::inflate),
    IMainAction {

    companion object {
        private const val ANIMATION_DURATION = 200L
        private const val EVENT_DUTY_STATUS = "event_duty_status"

        fun newInstance(context: Context) = newIntent<MainActivity>(context)
    }


    private lateinit var navController: NavController

    private val eventViewModel: EventViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
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

        rules.setOnClickListener {
            navController.navigate(R.id.rulesFragment)
        }
    }

    override fun setupView() {
        eventViewModel.setEventsMock()

        LocalBroadcastManager.getInstance(this).apply {
            registerReceiver(driverSwapReceiver, IntentFilter(BROADCAST_SWAP_DRIVERS))
        }

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
        navController = navHostFragment.navController
        binding.bottomNavigation.root.isSingleSelection = true
        setupNavController()
    }

    private fun setupNavController() {
        if (::navController.isInitialized) {
            checkLastDutyStatus()

            binding.bottomNavigation.root.addOnButtonCheckedListener { group, checkedId, isChecked ->
                var eventDutyStatus: EventCode? = null
                when (checkedId) {
                    R.id.coDriverFragment -> {
                        eventViewModel.removeCurrentDutyStatus()
                        navigateTo(id = checkedId)
                        return@addOnButtonCheckedListener
                    }

                    R.id.homeFragment -> {
                        eventViewModel.removeCurrentDutyStatus()
                        navigateTo(id = checkedId)
                        return@addOnButtonCheckedListener
                    }

                    R.id.breakInFragment -> {
                        eventDutyStatus = EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_SLEEPER_BERTH
                    }

                    R.id.offFragment -> {
                        eventDutyStatus = EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY
                    }

                    R.id.onFragment -> {
                        eventDutyStatus = EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING
                    }

                    R.id.drivingFragment -> {
                        eventDutyStatus = EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING
                    }
                }

                eventDutyStatus?.let { dutyStatus ->
                    navigateTo(id = R.id.eventCalculationFragment, dutyStatus = dutyStatus)
                }
            }
        }
    }

    private fun checkLastDutyStatus() {
        // If last duty status is not null, then navigate to appropriate fragment
        eventViewModel.getCurrentDutyStatus()?.let { dutyStatus ->
            navigateByDutyStatus(status = dutyStatus)
            return
        }

        // Else navigate to default start destination
        navController.graph.startDestinationId.also {
            navigateTo(id = it)
            binding.bottomNavigation.root.check(it)
        }
    }

    private fun navigateTo(id: Int) = navController.navigate(id)

    private fun navigateTo(id: Int, dutyStatus: EventCode) {
        eventViewModel.storeCurrentDutyStatus(status = dutyStatus)
        navController.navigate(id, bundleOf(EVENT_DUTY_STATUS to dutyStatus))
    }

    private fun navigateByDutyStatus(status: EventCode) {
        navController.navigate(R.id.eventCalculationFragment, bundleOf(EVENT_DUTY_STATUS to status))
        setSelectedItemByStatus(status)
    }

    private fun setSelectedItemByStatus(status: EventCode) {
        val id = when (status) {
            EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_SLEEPER_BERTH -> R.id.breakInFragment
            EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY -> R.id.offFragment
            EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING -> R.id.onFragment
            EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING -> R.id.drivingFragment
            else -> null
        }

        id?.let { binding.bottomNavigation.root.check(it) }
    }


    override fun onResume() {
        super.onResume()
        connectionStateMonitor.enable()
        when (connectionStateMonitor.hasNetworkConnection()) {
            true -> onPositive()
            else -> onNegative()
        }
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
            when (events.isNotEmpty()) {
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
        val navView = binding.bottomNavigation.root

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
        val navView = binding.bottomNavigation.root

        if (navView.isGone) return

        binding.bottomNavigation.root.also {
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
}