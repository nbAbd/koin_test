package com.pieaksoft.event.consumer.android.ui.events

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pieaksoft.event.consumer.android.databinding.FragmentRecordsCertificationBinding
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.EventInsertCode
import com.pieaksoft.event.consumer.android.model.EventInsertType
import com.pieaksoft.event.consumer.android.model.Location
import com.pieaksoft.event.consumer.android.ui.IMainAction
import com.pieaksoft.event.consumer.android.ui.base.BaseAdapter
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import com.pieaksoft.event.consumer.android.utils.formatToServerDateDefaults
import com.pieaksoft.event.consumer.android.utils.formatToServerTimeDefaults
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
            EventInsertType.certificate.type,
            EventInsertCode.FirstCertification.code,
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

        viewModel.certifiedEvent.observe(this, { event ->
            // viewModel.getEventList()
            //  findNavController().popBackStack()

        })

        viewModel.certifiedDate.observe(this) { date ->
            val refList = certificationAdapter.list.filter { it != date }
            if (refList.isEmpty()) findNavController().popBackStack()
            else certificationAdapter.update(ArrayList(refList))
        }
    }
}