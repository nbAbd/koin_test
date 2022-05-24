package com.pieaksoft.event.consumer.android.ui.activities.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Window
import androidx.core.app.NotificationManagerCompat
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
import com.pieaksoft.event.consumer.android.ui.profile.ProfileViewModel
import com.pieaksoft.event.consumer.android.utils.*
import com.pieaksoft.event.consumer.android.utils.EventManager.isNetworkEnable
import com.pieaksoft.event.consumer.android.utils.receivers.PhoneRebootReceiver
import com.pieaksoft.event.consumer.android.views.Dialogs
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivityNew<ActivityMainBinding>(ActivityMainBinding::inflate),
    IMainAction {

    companion object {
        private const val ANIMATION_DURATION = 200L

        fun newInstance(context: Context) = newIntent<MainActivity>(context)
    }


    lateinit var navController: NavController

    private val eventViewModel: EventViewModel by viewModel()

    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setupView()
        setupBottomNavigationView()
        setCustomActionBar()
    }

    private fun setCustomActionBar() = with(binding.appBar) {
        setSupportActionBar(toolbar)
        menu.setOnClickListener { navController.navigate(R.id.action_show_menu) }

        log.setOnClickListener { navController.navigate(R.id.action_show_log) }

        dotInspect.setOnClickListener {
            if (eventViewModel.eventListRequiresCertification.value?.isNotEmpty() == true) {
                showCustomSnackBar("First certify events", type = SnackBarType.ERROR)
            } else {
                navController.navigate(R.id.action_show_dot_inspect)
            }
        }

        rules.setOnClickListener { navController.navigate(R.id.rulesFragment) }
    }

    override fun setupView() {
        eventViewModel.sendLoginEvent()
        profileViewModel.getDriversInfo()
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
        binding.bottomNavigation.root.check(R.id.offFragment)
        setupNavController()
    }

    private fun setupNavController() {
        if (::navController.isInitialized) {
            checkLastDutyStatus()

            binding.bottomNavigation.root.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (!isChecked) return@addOnButtonCheckedListener

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

                eventDutyStatus?.let {
                    if (checkedId != EventManager.events.lastItemEventCode.itemId) {
                        eventViewModel.setEventInsertCode(code = it)

                        Dialogs.showInsertEventDialogFragment(supportFragmentManager) { isCanceled ->
                            if (isCanceled) navigateTo(status = EventManager.events.lastItemEventCode)
                            else navigateTo(status = eventDutyStatus)
                        }
                    } else navigateTo()
                }
            }
        }
    }

    private fun checkLastDutyStatus() {
        // If last duty status is not null, then navigate to appropriate fragment
        eventViewModel.getCurrentDutyStatus()?.let { status ->
            navigateTo(status = status)
            return
        }

        // Else navigate to default start destination
        navController.graph.startDestinationId.also {
            navigateTo(id = it)
            binding.bottomNavigation.root.check(it)
        }
    }

    fun navigateTo(
        id: Int = R.id.eventCalculationFragment,
        status: EventCode? = null
    ) {
        status?.let {
            eventViewModel.storeCurrentDutyStatus(status = status)
            status.itemId?.let {
                if (it != binding.bottomNavigation.root.checkedButtonId) {
                    binding.bottomNavigation.root.check(it)
                }
            }
        }
        navController.navigate(id)
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
        eventViewModel.eventListRequiresCertification.observe(this) { events ->
            when (events.isNotEmpty()) {
                true -> showCertificationNeedView()
                else -> hideCertificationNeedView()
            }
        }

        eventViewModel.eventList.observe(this) {
            val isPhoneRebooted =
                eventViewModel.sp.getBoolean(PhoneRebootReceiver.IS_PHONE_REBOOTED, false)
            if (isPhoneRebooted) {
                eventViewModel.apply {
                    sp.put(PhoneRebootReceiver.IS_PHONE_REBOOTED, false)
                    getLastDrivingLogEvent(it)?.let { it1 ->
                        syncRemainingIntermediateLogs(it1, this@MainActivity)
                    }
                }
            }
        }

        eventViewModel.progress.observe(this) {
            setProgressVisible(it)
        }

        eventViewModel.error.observe(this) {
            ErrorHandler.showError(binding.root, it)
        }
    }

    private fun showPermissionsDialog() {
        PermissionDialog().apply { show(supportFragmentManager, PermissionDialog::class.java.name) }
    }

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

    private val EventCode.itemId: Int?
        get() = when (this) {
            EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_SLEEPER_BERTH -> R.id.breakInFragment
            EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY -> R.id.offFragment
            EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING -> R.id.onFragment
            EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING -> R.id.drivingFragment
            else -> null
        }

    override fun onDestroy() {
        eventViewModel.sendLogoutEvent()
        super.onDestroy()
    }
}