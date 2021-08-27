package com.pieaksoft.event.consumer.android.ui.codriver

import android.util.Log
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.network.ErrorHandler
import com.pieaksoft.event.consumer.android.ui.profile.ProfileVM
import com.pieaksoft.event.consumer.android.utils.toast
import org.koin.android.viewmodel.ext.android.viewModel


class CoDriverFragment: BaseFragment(R.layout.fragment_co_driver) {

    private val profileVM: ProfileVM by viewModel()

    override fun setViews() {
        profileVM.getProfile()
     //   driver2.setEmpty(true)
    }

    override fun bindVM() {
        profileVM.error.observe(this, { message ->
            Log.e("test_log", "test error = " + message)
            message?.let {
                toast(ErrorHandler.getErrorMessage(it, requireContext()))
            }
        })
    }
}