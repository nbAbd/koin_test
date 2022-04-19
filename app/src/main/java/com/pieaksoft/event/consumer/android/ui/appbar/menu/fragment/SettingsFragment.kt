package com.pieaksoft.event.consumer.android.ui.appbar.menu.fragment

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentSettingsBinding
import com.pieaksoft.event.consumer.android.ui.activities.main.MainActivity
import com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter.SettingsAdapter
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment


class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private lateinit var navController: NavController

    val settingData: List<SettingsAdapter.SettingItem> by lazy {
        listOf(
            SettingsAdapter.SettingItem.SIGNATURE
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavController()
    }

    private val settingsAdapter: SettingsAdapter by lazy {
        SettingsAdapter { value -> onClickItem(value) }
    }

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
                    when {
                        settingData.size == 1 -> {
                            super.getItemOffsets(outRect, view, parent, state)
                        }
                        itemPosition == state.itemCount.minus(1) -> {
                            outRect.setEmpty()
                        }
                        else -> {
                            super.getItemOffsets(outRect, view, parent, state)
                        }
                    }
                }
            }.also {
                it.drawable?.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    ), PorterDuff.Mode.SRC_IN
                )
                addItemDecoration(it)
            }
            adapter = settingsAdapter
        }
    }

    private fun setupNavController() = with(binding) {
        val navCont = (activity as MainActivity).navController
        navController = navCont
    }

    private fun onClickItem(item: SettingsAdapter.SettingItem) {
        when (item) {
            SettingsAdapter.SettingItem.SIGNATURE -> navController.navigate(R.id.signatureFragment)
        }
    }
}