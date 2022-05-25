package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentEventCalculationBinding
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.events.EventViewModel
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
        eventViewModel.getEventList()
    }

    override fun observe() {
        viewModel.onDutyBreakIn.observe(this) {
            if (it < 0) {
                binding.breakProgress.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.red)
            } else {
                binding.breakProgress.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }
            binding.breakInValue.text = hmsTimeFormatter(it)
            binding.breakProgress.progress =
                ((it.toFloat() / 60000 / ON_DUTY_BREAK_IN_MINUTES) * 100)
        }

        viewModel.onDuty.observe(this) {
            if (it < 0) {
                binding.onDutyLimitDayProgress.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.red)
                binding.onDutyLimitDayProgress.setProgressWithAnimation(100f, 1000)
            } else {
                binding.onDutyLimitDayProgress.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
                binding.onDutyLimitDayProgress.progress =
                    ((it.toFloat() / 60000 / ON_DUTY_WINDOW_MINUTES) * 100)
            }

            binding.onValue.text = hmsTimeFormatter(it)
        }

        viewModel.maxOnDuty.observe(this) {
            if (it < 0) {
                binding.onDutyLimitMonthProgress.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.red)
                binding.onDutyLimitMonthProgress.setProgressWithAnimation(100f, 1000)
            } else {
                binding.onDutyLimitMonthProgress.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)

                binding.onDutyLimitMonthProgress.progress =
                    ((it.toFloat() / 1000 / 60 / 60) * 100)
            }
            binding.dutyCycle.text = hmsTimeFormatter(it)
        }

        viewModel.onDutyDrivingLimit.observe(this) {
            binding.drivingLimit.text = hmsTimeFormatter2(it)
        }

        viewModel.onDutyExceedingTheLimitWarning.observeOnce(this) {
            if (it) toast("Warning!\n You continuously on duty more 14 hour")
        }

        viewModel.maxOnDutyExceedingTheLimitWarning.observeOnce(this) {
            if (it) toast("Warning!\n You are on duty more 70 hours")
        }

        viewModel.onDutyBreakInTheLimitWarning.observeOnce(this) {
            if (it) toast("Warning!\n You are driving more than 8 hours, you must take a break")
        }
    }

    private fun initialize() {
        eventViewModel.eventList.observe(viewLifecycleOwner) {
            // Check last selected status
            if (eventStatusCode != EventManager.events.lastItemEventCode) {
                performStatusChange()
                return@observe
            }

            // Reset timer
            viewModel.resetAll()

            // Start calculation
            calculate()
        }
    }

    private fun calculate() {
        viewModel.calculate { startTimer() }
    }

    private fun startTimer() = with(viewModel) {
        if (eventStatusCode != null) {
            // Stop countdown after changing status
            stopCountdown()

            when (eventStatusCode) {
                EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING -> {
                    startTotalOnDutyCounter()
                    startOnDutyCounter()
                }

                EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING -> {
                    startTotalOnDutyCounter()
                    startOnDutyCounter()
                    startDrivingLimitCounter()
                    startBreakInCounter()
                }
                else -> Unit
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopCountdown()
    }


    /**
     * Call this method to stop count down
     */
    private fun stopCountdown() {
        viewModel.apply {
            cancelTotalOnDutyCounter()
            cancelOnDutyCounter()
            cancelDrivingLimitCounter()
            cancelBreakInCounter()
        }
    }
}