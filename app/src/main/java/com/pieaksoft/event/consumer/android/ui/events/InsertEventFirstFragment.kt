package com.pieaksoft.event.consumer.android.ui.events

import com.pieaksoft.event.consumer.android.databinding.FragmentInsertEventFirstBinding
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel

class InsertEventFirstFragment :
    BaseMVVMFragment<FragmentInsertEventFirstBinding, EventViewModel>() {
    private lateinit var pagerListener: InsertEventListener

    companion object {
        fun newInstance(pagerListener: InsertEventListener) = InsertEventFirstFragment().apply {
            this.pagerListener = pagerListener
        }
    }

    override val viewModel: EventViewModel by sharedViewModel()

    override fun setupView() = with(binding) {
        next.setOnClickListener { pagerListener.onNext() }
        backInsertBtn.setOnClickListener { pagerListener.onBack() }
    }

    override fun observe() {
    }
}