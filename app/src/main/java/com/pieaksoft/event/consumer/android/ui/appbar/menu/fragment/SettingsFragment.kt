package com.pieaksoft.event.consumer.android.ui.appbar.menu.fragment

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentSettingsBinding
import com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter.SettingsAdapter
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment


class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private lateinit var navController: NavController

    private val settingData: List<String> by lazy {
        listOf(
            "Signature"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavController()
    }

    private val settingsAdapter: SettingsAdapter by lazy { SettingsAdapter { v -> onClickItem(v) } }

    init {
        requiresBottomNavigation = false
    }

    override fun setupView() {
        settingsAdapter.list = settingData
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            val decoration = object : DividerItemDecoration(requireContext(), VERTICAL) {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val itemPosition = parent.getChildAdapterPosition(view)
                    // hide divider for the last child
                    if (itemPosition == state.itemCount.minus(1)) {
                        outRect.setEmpty()
                    } else {
                        super.getItemOffsets(outRect, view, parent, state)
                    }
                }
            }.also {
                it.drawable?.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    ), PorterDuff.Mode.SRC_IN
                )
            }
            addItemDecoration(decoration)
            adapter = settingsAdapter
        }
    }

    private fun setupNavController() = with(binding) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun onClickItem(itemName: String) {
        when (itemName) {
            "Signature" -> navController.navigate(R.id.signatureFragment)
        }
    }
}