package com.pieaksoft.event.consumer.android.ui.events

import androidx.navigation.fragment.findNavController
import com.pieaksoft.event.consumer.android.databinding.FragmentInsertEventBinding
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel

class InsertEventFragment :
    BaseMVVMFragment<FragmentInsertEventBinding, EventViewModel>() {

    override val viewModel: EventViewModel by sharedViewModel()

    override fun setupView() {

    }

    override fun observe() {
        viewModel.event.observe(this, {
            viewModel.getEventList()
            findNavController().popBackStack()
        })

        viewModel.localEvent.observe(this, {
            viewModel.getEventList()
            findNavController().popBackStack()
        })
    }
}