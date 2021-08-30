package com.pieaksoft.event.consumer.android.ui.codriver

import android.util.Log
import by.kirich1409.viewbindingdelegate.viewBinding
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentCoDriverBinding
import com.pieaksoft.event.consumer.android.network.ErrorHandler
import com.pieaksoft.event.consumer.android.ui.profile.ProfileVM
import com.pieaksoft.event.consumer.android.utils.toast
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel


class CoDriverFragment: BaseFragment(R.layout.fragment_co_driver) {
    private val binding by viewBinding(FragmentCoDriverBinding::bind)
    private val profileVM: ProfileVM by viewModel()

    override fun setViews() {
        profileVM.getProfile()
        binding.driver2.setEmpty(true)

    }

    override fun bindVM() {

        profileVM.driver1.observe(this, {
            launch {
                binding.driver1.setDriverInfo(it)
            }

        })

        profileVM.error.observe(this, { message ->
            message?.let {
                toast(ErrorHandler.getErrorMessage(it, requireContext()))
            }
        })
    }
}