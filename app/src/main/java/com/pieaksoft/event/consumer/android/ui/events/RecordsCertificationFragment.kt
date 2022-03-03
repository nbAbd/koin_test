package com.pieaksoft.event.consumer.android.ui.events

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pieaksoft.event.consumer.android.databinding.FragmentRecordsCertificationBinding
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.enums.EventInsertType
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.model.event.Location
import com.pieaksoft.event.consumer.android.ui.activities.main.IMainAction
import com.pieaksoft.event.consumer.android.ui.base.BaseAdapter
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import com.pieaksoft.event.consumer.android.ui.events.adapter.EventCertificationAdapter
import com.pieaksoft.event.consumer.android.utils.formatToServerDateDefaults
import com.pieaksoft.event.consumer.android.utils.formatToServerTimeDefaults
import com.pieaksoft.event.consumer.android.utils.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*
import kotlin.collections.ArrayList

class RecordsCertificationFragment :
    BaseMVVMFragment<FragmentRecordsCertificationBinding, EventViewModel>() {
    override val viewModel: EventViewModel by sharedViewModel()

    private val certificationAdapter = EventCertificationAdapter()

    private val tempEvent: Event by lazy {
        Event(
            "",
            EventInsertType.DRIVERS_CERTIFICATION_RE_CERTIFICATION_OF_RECORDS.type,
            EventCode.DRIVER_FIRST_CERTIFICATION_OF_DAILY_RECORD.code,
            date = Date().formatToServerDateDefaults(),
            time = Date().formatToServerTimeDefaults(),
            Location(-10.12345f, 48.23432f),
            shippingDocumentNumber = "test",
            totalEngineHours = 20,
            totalEngineMiles = 450,
            eventRecordOrigin = "AUTOMATICALLY_RECORDED_BY_ELD",
            eventRecordStatus = "ACTIVE",
            malfunctionIndicatorStatus = "NO_ACTIVE_MALFUNCTION",
            dataDiagnosticEventIndicatorStatus = "NO_ACTIVE_DATA_DIAGNOSTIC_EVENTS_FOR_DRIVER",
            driverLocationDescription = "chicago, IL",
            dutyStatus = "OFF_DUTY",
            certification = null
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
        viewModel.eventListRequiresCertification.observe(viewLifecycleOwner, { events ->
            if (events.isNotEmpty()) {
                certificationAdapter.list = ArrayList(events.groupBy { it.date ?: "" }.keys)
            } else {
                findNavController().popBackStack()
            }
        })

        viewModel.certifiedDate.observe(this) { date ->
            date?.let {
                certificationAdapter.list.filter { it != date }.also { events ->
                    if (events.isEmpty()) findNavController().popBackStack()
                    else certificationAdapter.update(ArrayList(events))
                }
            }
        }

        viewModel.error.observe(this, {
            toast(it.message ?: "")
        })
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