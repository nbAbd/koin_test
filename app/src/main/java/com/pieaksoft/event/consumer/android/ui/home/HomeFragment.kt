package com.pieaksoft.event.consumer.android.ui.home

import com.pieaksoft.event.consumer.android.databinding.FragmentHomeBinding
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment
import com.pieaksoft.event.consumer.android.utils.Modifier
import com.pieaksoft.event.consumer.android.utils.performStatusChange

class HomeFragment : BaseFragment<FragmentHomeBinding>(), Modifier {
    init {
        requiresActionBar = false
    }

    override fun setupView() {
        binding.closeBtn.setOnClickListener { performStatusChange() }
    }
}