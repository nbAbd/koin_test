package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.widget.Toast
import androidx.core.content.ContextCompat
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentEventCalculationBinding
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import com.pieaksoft.event.consumer.android.utils.hmsTimeFormatter
import com.pieaksoft.event.consumer.android.utils.hmsTimeFormatter2
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class EventsCalculationFragment :
    BaseMVVMFragment<FragmentEventCalculationBinding, EventCalculationViewModel>() {

    init {
        requiresActionBar = true
    }

    override val viewModel: EventCalculationViewModel by viewModel()
    private val eventViewModel: EventViewModel by sharedViewModel()
    private lateinit var eventStatusCode: EventCode

    override fun setupView() {
        eventViewModel.getCurrentDutyStatus()?.let {
            eventStatusCode = it
        }
    }

    override fun onResume() {
        super.onResume()
        eventViewModel.getEventList()
    }

    override fun observe() {
        viewModel.onDutyBreakIn.observe(this, {
            if (it < 0) {
                binding.breakProgressBar.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.red)
            } else {
                binding.breakProgressBar.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }
            binding.breakInValue.text = hmsTimeFormatter(it)
            binding.breakProgressBar.progress =
                ((it.toFloat() / 60000 / on_Duty_Break_In_Minutes) * 100)
        })

        viewModel.onDuty.observe(this, {
            if (it < 0) {
                binding.progressBar2.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.red)
                binding.progressBar2.setProgressWithAnimation(100f, 1000)
            } else {
                binding.progressBar2.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
                binding.progressBar2.progress =
                    ((it.toFloat() / 60000 / on_Duty_Window_Minutes) * 100)
            }


            binding.onValue.text = hmsTimeFormatter(it)
        })

        viewModel.maxOnDuty.observe(this, {
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
        })

        viewModel.onDutyDrivingLimit.observe(this, {
            binding.drivingLimit.text = hmsTimeFormatter2(it)
        })

        viewModel.onDutyExceedingTheLimitWarning.observe(this, {
            Toast.makeText(
                requireContext(),
                "Warning!\n You continuously on duty more 14 hour",
                Toast.LENGTH_SHORT
            ).show()
        })


        eventViewModel.eventList.observe(this, {
            viewModel.apply {
                resetMillis()
                calculate {
                    if (::eventStatusCode.isInitialized) {
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
            }
        })
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