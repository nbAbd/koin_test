package com.pieaksoft.event.consumer.android.ui.events

import com.pieaksoft.event.consumer.android.databinding.FragmentInsertEventSecondBinding
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.EventInsertType
import com.pieaksoft.event.consumer.android.model.Location
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import com.pieaksoft.event.consumer.android.utils.formatToServerDateDefaults
import com.pieaksoft.event.consumer.android.utils.formatToServerTimeDefaults
import org.koin.android.viewmodel.ext.android.sharedViewModel

class InsertEventSecondFragment :
    BaseMVVMFragment<FragmentInsertEventSecondBinding, EventViewModel>() {
    private lateinit var pagerListener: InsertEventListener
    private val eventModel: Event by lazy {
        Event(
            "",
            eventType = EventInsertType.statusChange.type,
            coordinates = Location(latitude = -10.12345f, longitude = 48.23432f),
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

    companion object {
        fun newInstance(pagerListener: InsertEventListener) = InsertEventSecondFragment().apply {
            this.pagerListener = pagerListener
        }
    }

    override val viewModel: EventViewModel by sharedViewModel()

    override fun setupView() = with(binding) {
        btnBack.setOnClickListener { pagerListener.onBack() }
        save.setOnClickListener { insertEvent() }
    }

    override fun observe() {
        viewModel.eventInsertCode.observe(viewLifecycleOwner, {
            eventModel.eventCode = it?.code
        })

        viewModel.eventInsertDate.observe(viewLifecycleOwner, {
            eventModel.date = it?.formatToServerDateDefaults() ?: ""
            eventModel.time = it?.formatToServerTimeDefaults() ?: ""
        })


        viewModel.event.observe(this, {
            it?.let {
                viewModel.getEventList()
                pagerListener.onComplete()
            }
        })

        viewModel.localEvent.observe(this, {
            it?.let {
                viewModel.getEventList()
                pagerListener.onComplete()
            }
        })
    }

    private fun insertEvent() = viewModel.insertEvent(e = eventModel)

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetInserting()
    }
}