package com.pieaksoft.event.consumer.android.ui.appbar.menu

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.pieaksoft.event.consumer.android.ui.MainActivity
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentMenuBinding
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment
import com.pieaksoft.event.consumer.android.ui.login.LoginActivity
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_CURRENT_USER_ID
import com.pieaksoft.event.consumer.android.utils.hide

class MenuFragment : BaseFragment<FragmentMenuBinding>() {
    companion object {
        private const val TOOLBAR_HIDE_ANIMATION_DURATION = 400L
    }

    init {
        requiresBottomNavigation = false
    }

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavController()
    }

    override fun setupView() {
        binding.apply {
            btnClose.setOnClickListener { findNavController().popBackStack() }
            toggleGroupTop.addOnButtonCheckedListener(menuGroupSelectedListener)
            toggleGroupBottom.addOnButtonCheckedListener(menuGroupSelectedListener)
            itemCanadaRules.addOnCheckedChangeListener(menuItemCheckListener)
            itemMd.addOnCheckedChangeListener(menuItemCheckListener)
            itemDriverVehicleInspectionReport.addOnCheckedChangeListener(menuItemCheckListener)
            itemTruckSettings.addOnCheckedChangeListener(menuItemCheckListener)
            itemSettings.addOnCheckedChangeListener(menuItemCheckListener)
            itemFaq.addOnCheckedChangeListener(menuItemCheckListener)
            itemLogout.addOnCheckedChangeListener(menuItemCheckListener)
        }
    }

    private val menuGroupSelectedListener =
        MaterialButtonToggleGroup.OnButtonCheckedListener { _, checkedId, _ ->
            with(binding) {
                if (toggleGroupTop.checkedButtonIds.contains(checkedId)) {
                    if (toggleGroupBottom.checkedButtonIds.isNotEmpty()) toggleGroupBottom.clearChecked()
                    else return@with
                }
                if (toggleGroupBottom.checkedButtonIds.contains(checkedId)) {
                    if (toggleGroupTop.checkedButtonIds.isNotEmpty()) toggleGroupTop.clearChecked()
                    else return@with
                }
            }
        }

    private val menuItemCheckListener = MaterialButton.OnCheckedChangeListener { button, isCheck ->
        when (button.id) {
            R.id.item_canada_rules -> navController.navigate(R.id.canada_rules_fragment)
            R.id.item_md -> navController.navigate(R.id.md_fragment)
            R.id.item_driver_vehicle_inspection_report -> Unit
            R.id.item_truck_settings -> Unit
            R.id.item_settings -> Unit
            R.id.item_faq -> Unit
            R.id.item_logout -> if (isCheck) showLogoutDialog()
        }
    }

    private fun setupNavController() = with(binding) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.menu_details_container_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onResume() {
        super.onResume()
        hideActionBar()
    }

    private fun hideActionBar() {
        val toolbar = (requireActivity() as MainActivity).binding.appBar.root
        if (toolbar.isGone) return

        val transition = Fade().apply {
            addTarget(toolbar)
            duration = TOOLBAR_HIDE_ANIMATION_DURATION
        }
        TransitionManager.beginDelayedTransition(binding.root, transition)
        toolbar.hide()
    }

    private fun logout() {
        sharedPrefs.edit().putString(SHARED_PREFERENCES_CURRENT_USER_ID, "").apply()
        startActivity(LoginActivity.newInstance(requireContext()))
        requireActivity().finish()
    }

    private fun showLogoutDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle(R.string.logout)
            .setMessage(R.string.logout_ask)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                logout()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
                binding.toggleGroupBottom.clearChecked()
            }.create()

        alertDialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        alertDialog.show()
    }
}