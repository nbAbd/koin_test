package com.pieaksoft.event.consumer.android.ui.events

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pieaksoft.event.consumer.android.databinding.FragmentRecordsCertificationBinding
import com.pieaksoft.event.consumer.android.enums.*
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.ui.activities.main.IMainAction
import com.pieaksoft.event.consumer.android.ui.base.BaseAdapter
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import com.pieaksoft.event.consumer.android.ui.events.adapter.EventCertificationAdapter
import com.pieaksoft.event.consumer.android.utils.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.collections.ArrayList

class RecordsCertificationFragment :
    BaseMVVMFragment<FragmentRecordsCertificationBinding, EventViewModel>() {
    override val viewModel: EventViewModel by sharedViewModel()

    private val certificationAdapter = EventCertificationAdapter()

    private val tempEvent: Event by lazy {
        Event(
            eventType = EventInsertType.DRIVERS_CERTIFICATION_RE_CERTIFICATION_OF_RECORDS.type,
            eventCode = EventCode.DRIVER_FIRST_CERTIFICATION_OF_DAILY_RECORD.code,
            date = viewModel.getFormattedUserDate(),
            time = viewModel.getFormattedUserTime(),
            eventRecordOrigin = EventRecordOriginType.AUTOMATICALLY_RECORDED_BY_ELD.type,
            eventRecordStatus = EventRecordStatusType.ACTIVE.type,
            malfunctionIndicatorStatus = MalfunctionIndicatorStatusType.NO_ACTIVE_MALFUNCTION.type,
            dataDiagnosticEventIndicatorStatus = DataDiagnosticEventIndicatorStatusType
                .NO_ACTIVE_DATA_DIAGNOSTIC_EVENTS_FOR_DRIVER.type
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerview()
    }

    override fun setupView() = with(binding) {
        (activity as IMainAction).hideCertificationNeedView()
        confirmCert.setOnClickListener {
            certificationAdapter.dateList.forEach { date ->
                viewModel.certifyEvent(date = date, event = tempEvent)
            }
        }

        cancelCert.setOnClickListener {
            findNavController().popBackStack()
        }

        btnClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerview() = with(binding) {
        certificationAdapter.setClickListener(object : BaseAdapter.ItemClickListener<String> {
            override fun onClick(position: Int, item: String) {
                confirmCert.isEnabled = certificationAdapter.dateList.isNotEmpty()
            }
        })

        certificationList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = certificationAdapter
        }
    }

    override fun observe() {
        viewModel.eventListRequiresCertification.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                certificationAdapter.list = ArrayList(events.groupBy { it.date ?: "" }.keys)
            } else {
                findNavController().popBackStack()
            }
        }

        viewModel.certifiedDate.observe(this) { date ->
            if (certificationAdapter.dateList.isNotEmpty()) certificationAdapter.dateList.removeFirst()
            if (certificationAdapter.dateList.isEmpty()) {
                date?.let {
                    certificationAdapter.list.filter { it != date }.also { events ->
                        if (events.isEmpty()) findNavController().popBackStack()
                        else certificationAdapter.update(ArrayList(events))
                    }
                }
            }
        }

        viewModel.error.observe(this) {
            toast(it.message ?: "")
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.getEventList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.certifiedDate.value = null
    }
}