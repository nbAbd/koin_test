package com.pieaksoft.event.consumer.android.ui.appbar

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentDotInspectBinding
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.Report
import com.pieaksoft.event.consumer.android.model.ReportType
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import com.pieaksoft.event.consumer.android.utils.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel

class DataTransferFragment :
    BaseMVVMFragment<FragmentDotInspectBinding, EventViewModel>() {
    init {
        requiresBottomNavigation = false
    }

    override val viewModel: EventViewModel by sharedViewModel()
    private var report: Report = Report()
    private val transferMethods by lazy { resources.getStringArray(R.array.transfer_methods) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinner()
    }

    override fun setupView() {
        with(binding) {
            closeBtn.setOnClickListener { findNavController().popBackStack() }
            cancelBtn.setOnClickListener { findNavController().popBackStack() }
            sendBtn.setOnClickListener {
                viewModel.sendReport(report = report) {
                    findNavController().popBackStack()
                }
            }

            comment.editText.doAfterTextChanged {
                report.comment = it.toString()
            }
        }
    }

    private fun setupSpinner() = with(binding.transferMethod) {
        getSpinner().apply {
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                transferMethods
            ).also { arrayAdapter -> setAdapter(arrayAdapter) }
        }

        getSpinner().setOnItemClickListener { _, _, position, _ ->
            report.reportType = ReportType.forPosition(position)
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner, { throwable ->
            toast(throwable.message.toString())
        })
    }
}