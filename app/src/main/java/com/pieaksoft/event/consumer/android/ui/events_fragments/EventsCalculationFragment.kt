package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentEventCalculationBinding
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.enums.EventInsertType
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.model.event.isDutyStatusChanged
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import com.pieaksoft.event.consumer.android.utils.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class EventsCalculationFragment :
    BaseMVVMFragment<FragmentEventCalculationBinding, EventCalculationViewModel>(), Modifier {

    init {
        requiresActionBar = true
    }

    override val viewModel: EventCalculationViewModel by viewModel()

    private val eventViewModel: EventViewModel by sharedViewModel()
    private var eventStatusCode: EventCode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }


    override fun setupView() {
        eventStatusCode = eventViewModel.getCurrentDutyStatus()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkResetDate()
    }

    override fun observe() {
        viewModel.onDutyBreakIn.observe(this) {
            if (it < 0) {
                binding.breakProgressBar.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.red)
            } else {
                binding.breakProgressBar.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }
            binding.breakInValue.text = hmsTimeFormatter(it)
            binding.breakProgressBar.progress =
                ((it.toFloat() / 60000 / ON_DUTY_BREAK_IN_MINUTES) * 100)
        }

        viewModel.onDuty.observe(this) {
            if (it < 0) {
                binding.progressBar2.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.red)
                binding.progressBar2.setProgressWithAnimation(100f, 1000)
            } else {
                binding.progressBar2.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
                binding.progressBar2.progress =
                    ((it.toFloat() / 60000 / ON_DUTY_WINDOW_MINUTES) * 100)
            }


            binding.onValue.text = hmsTimeFormatter(it)
        }

        viewModel.maxOnDuty.observe(this) {
            if (it < 0) {
                binding.progressBar3.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
                binding.progressBar3.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.red)
                binding.progressBar3.setProgressWithAnimation(100f, 1000)
            } else {
                binding.progressBar3.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)

                binding.progressBar3.progress =
                    ((it.toFloat() / 1000 / 60 / 60) * 100)
            }
            binding.dutyCycle.text = hmsTimeFormatter(it)
        }

        viewModel.onDutyDrivingLimit.observe(this) {
            binding.drivingLimit.text = hmsTimeFormatter2(it)
        }

        viewModel.onDutyExceedingTheLimitWarning.observe(this) {
            Toast.makeText(
                requireContext(),
                "Warning!\n You continuously on duty more 14 hour",
                Toast.LENGTH_SHORT
            ).show()
        }


        /**
         * Called only once when RESET_CYCLE request called
         */
        eventViewModel.event.observeOnce(viewLifecycleOwner) {
            eventViewModel.getEventList()
        }

        eventViewModel.localEvent.observeOnce(viewLifecycleOwner) {
            eventViewModel.getEventList()
        }
    }


    private fun initialize() {
        eventViewModel.eventList.observe(viewLifecycleOwner) { events ->
            // Check last selected status
            if (eventStatusCode != Storage.eventList.lastItemEventCode) {
                performStatusChange()
                return@observe
            }

            // Reset timer
            viewModel.resetTimer()

            // Store first event date
            Storage.eventList.elementAtOrNull(0)?.let {
                viewModel.sp.storeResetCycleStartDate(it.date)
            }

            // Checking for CYCLE_RESET event
            checkCycleReset(events = events)

            calculate()
        }
    }

    private fun calculate() {
        viewModel.calculate { startTimer() }
    }

    private fun startTimer() = with(viewModel) {
        if (eventStatusCode != null) {
            when (eventStatusCode) {
                EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING -> {
                    // Cancel
                    cancelTotalOnDutyCounter()
                    cancelOnDutyCounter()
                    cancelDrivingLimitCounter()
                    cancelBreakInCounter()

                    // Count
                    startTotalOnDutyCounter()
                    startOnDutyCounter()
                }

                EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING -> {
                    // Cancel
                    cancelTotalOnDutyCounter()
                    cancelOnDutyCounter()
                    cancelDrivingLimitCounter()
                    cancelBreakInCounter()

                    // Count
                    startTotalOnDutyCounter()
                    startOnDutyCounter()
                    startDrivingLimitCounter()
                    startBreakInCounter()
                }
                else -> Unit
            }
        }
    }

    /**
     * Checks reset cycle event, then splits list from that index
     *
     * @param [events] List of events from API/DB
     */
    private fun checkCycleReset(events: List<Event>) {
        var resetCycleIndex = 0
        events.forEachIndexed { index, event ->
            if (event.eventType == EventInsertType.CYCLE_RESET.type) {
                resetCycleIndex = index

                // Store CYCLE_RESET event date
                viewModel.sp.storeResetCycleStartDate(event.date)
            }
        }


        // Split list from resetCycleIndex
        // List contains all events without filter
        val eventsAfterResetCycle = events.subList(resetCycleIndex, events.size)

        // Filter list only for duty status change
        Storage.eventList = eventsAfterResetCycle.filter { it.isDutyStatusChanged() }
    }

    override fun onStop() {
        super.onStop()
        viewModel.apply {
            cancelTotalOnDutyCounter()
            cancelOnDutyCounter()
            cancelDrivingLimitCounter()
            cancelBreakInCounter()
        }
    }
}