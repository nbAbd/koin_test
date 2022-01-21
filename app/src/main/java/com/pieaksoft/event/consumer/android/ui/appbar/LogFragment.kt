package com.pieaksoft.event.consumer.android.ui.appbar

import android.util.Log
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentLogBinding
import com.pieaksoft.event.consumer.android.databinding.InsertEventView2Binding
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.EventInsertCode
import com.pieaksoft.event.consumer.android.model.EventInsertType
import com.pieaksoft.event.consumer.android.model.Location
import com.pieaksoft.event.consumer.android.network.ErrorHandler
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import com.pieaksoft.event.consumer.android.ui.events.EventsAdapter
import com.pieaksoft.event.consumer.android.utils.*
import com.pieaksoft.event.consumer.android.views.Dialogs
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

class LogFragment : BaseMVVMFragment<FragmentLogBinding, EventViewModel>() {
    override val viewModel: EventViewModel by sharedViewModel()

    private val eventsAdapter by lazy { EventsAdapter() }
    private var sliderPosition: Int = 0
    private var insertEvent: String = ""
    private var insertEventDate: Date? = null


    override fun setupView() {
        binding.apply {
            closeLog.setOnClickListener {
                findNavController().popBackStack()
            }

            insertBtn.setOnClickListener {
                Dialogs.showInsertEventDialog(requireActivity(), object : Dialogs.EventInsertClick {
                    override fun onEventClick(event: EventInsertCode) {
                        insertEvent = event.code
                        Dialogs.showDateTimeSelector(
                            requireContext(),
                            object : Dialogs.DateSelectListener {
                                override fun onDateSelect(date: Date) {
                                    insertEventDate = date
                                    findNavController().navigate(R.id.action_show_insert_event)
                                }
                            })
                    }
                })

            }
        }
        setupRecyclerview()
    }

    private fun setupRecyclerview() {
        // eventList -> rename
        binding.eventsList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = eventsAdapter
            attachSnapHelperWithListener(
                PagerSnapHelper(),
                SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
                object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        sliderPosition = position
                        launch {
                            binding.dateText.text =
                                eventsAdapter.list.keys.elementAt(position).getDateFromString()
                                    .formatToServerDateDefaults2()
                        }
                    }

                    override fun onSnapPositionDragging() = Unit

                    override fun onSnapPositionNotChange(position: Int) {
                        launch {
                            if (position > 0) {
                                binding.dateText.text =
                                    eventsAdapter.list.keys.elementAt(position).getDateFromString()
                                        .formatToServerDateDefaults2()
                            }
                        }
                    }

                })
        }
    }

    private fun setEvents() {
        eventsAdapter.list = viewModel.getEventsGroupByDate()
        eventsAdapter.notifyDataSetChanged()

        binding.dateText.text = eventsAdapter.list.keys.elementAtOrNull(0)?.getDateFromString()
            ?.formatToServerDateDefaults2() ?: ""
    }

    // pager koiush kerek
    private fun save() {
        val insertEvent2Binding = InsertEventView2Binding.inflate(layoutInflater)
        insertEvent2Binding.save.setOnClickListener {
            val event = Event(
                "",
                EventInsertType.statusChange.type,
                insertEvent,
                date = insertEventDate?.formatToServerDateDefaults() ?: "",
                time = insertEventDate?.formatToServerTimeDefaults() ?: "",
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
            viewModel.insertEvent(event)
        }
    }

    override fun observe() {
        /*eventsViewModel.eventLiveData.observe(this, {
          findViewById<ConstraintLayout>(R.id.insert_event_view2).hide()
          eventsViewModel.getEventList()
      })*/

        /*eventsViewModel.eventDBLiveData.observe(this, {
            findViewById<ConstraintLayout>(R.id.insert_event_view2).hide()
            eventsViewModel.getEventList(true)
        })*/

        /*eventsViewModel.eventCertLiveData.observe(this, {
            findViewById<ConstraintLayout>(R.id.cert_view).hide()
            eventsViewModel.getEventList()
        })*/

        viewModel.eventList.observe(this, {
            setEvents()
        })

        viewModel.eventListByDate.observe(this, {
            setEvents()
        })

        viewModel.progress.observe(this, {
            //setProgressVisible(it)
        })

        viewModel.error.observe(this, {
            val error = ErrorHandler.getErrorMessage(it, requireContext())
            Log.e("test_logerrror", "test insert error response = $error")
        })
    }
}