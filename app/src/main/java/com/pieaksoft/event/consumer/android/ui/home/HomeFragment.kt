package com.pieaksoft.event.consumer.android.ui.home

import com.pieaksoft.event.consumer.android.databinding.FragmentHomeBinding
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    companion object {
        const val DRIVING_LIMIT_DEFAULT_TEXT = "11 h 00 m"
    }

    init {
        requiresActionBar = true
    }

    override fun setupView() {
        binding.drivingLimit.text = DRIVING_LIMIT_DEFAULT_TEXT
    }
}