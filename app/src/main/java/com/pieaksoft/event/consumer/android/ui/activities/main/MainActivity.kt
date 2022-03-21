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
import androidx.core.view.isGone
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.araujo.jordan.excuseme.ExcuseMe
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.ActivityMainBinding
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

        fun newInstance(context: Context) = newIntent<MainActivity>(context)
    }


    lateinit var navController: NavController

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
        setupWithNavController(navController = navHostFragment.navController)
    }

    private fun setupWithNavController(navController: NavController) {
        val startDestination = navController.graph.startDestinationId
        navigateTo(id = startDestination)
        binding.bottomNavigation.root.check(startDestination)
        binding.bottomNavigation.root.addOnButtonCheckedListener { group, checkedId, isChecked ->
            navigateTo(id = checkedId)
        }
    }

    private fun navigateTo(id: Int) = navController.navigate(id)


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