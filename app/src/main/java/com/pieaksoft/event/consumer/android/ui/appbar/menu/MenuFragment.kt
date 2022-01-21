package com.pieaksoft.event.consumer.android.ui.appbar.menu

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import androidx.transition.TransitionManager
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
            btnClose.setOnClickListener {
                findNavController().popBackStack()
            }

            itemCanadaRules.setOnClickListener {
                navController.navigate(R.id.canada_rules_fragment)
            }

            itemMd.setOnClickListener {
                navController.navigate(R.id.md_fragment)
            }

            itemLogout.setOnClickListener {
                logout()
            }
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
}