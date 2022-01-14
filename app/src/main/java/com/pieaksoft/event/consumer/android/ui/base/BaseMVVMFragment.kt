package com.pieaksoft.event.consumer.android.ui.base

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding

abstract class BaseMVVMFragment<VB : ViewBinding, VM : BaseViewModel> : BaseFragment<VB>() {
    protected abstract val viewModel: VM
    protected abstract fun observe()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observe()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupView() {

    }
}