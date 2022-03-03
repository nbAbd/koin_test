package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.widget.Toast
import androidx.core.content.ContextCompat
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentHomeBinding
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import com.pieaksoft.event.consumer.android.utils.hmsTimeFormatter
import com.pieaksoft.event.consumer.android.utils.hmsTimeFormatter2
import org.koin.android.viewmodel.ext.android.sharedViewModel

class EventsCalculationFragment :
    BaseMVVMFragment<FragmentHomeBinding, EventsCalculationViewModel>() {
    init {
        requiresActionBar = true
    }

    override val viewModel: EventsCalculationViewModel by sharedViewModel()
    private val eventViewModel: EventViewModel by sharedViewModel()

    override fun setupView() {}

    override fun onResume() {
        super.onResume()
        eventViewModel.getEventList()
        viewModel.calculateEvents()
    }

    override fun observe() {
        viewModel.drivingEventLiveData.observe(this, {
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

        viewModel.onEventLiveData.observe(this, {
            if (it < 0) {
                Toast.makeText(
                    requireContext(),
                    "Warning!\n You continuously on duty more 14 hour",
                    Toast.LENGTH_SHORT
                ).show()
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

        viewModel.dutyCycleEventLiveData.observe(this, {
            if (it < 0) {
                Toast.makeText(
                    requireContext(),
                    "Warning!\n You on duty more 70 hour",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar3.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
                binding.progressBar3.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.red)
                binding.progressBar3.setProgressWithAnimation(100f, 1000)
            } else {
                binding.progressBar3.progressBarColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)

                binding.progressBar3.progress =
                    ((it.toFloat() / 60000 / on_Duty_Cycle_Minutes) * 100)
            }
            binding.dutyCycle.text = hmsTimeFormatter(it)
        })

        viewModel.drivingLimitLiveData.observe(this, {
            binding.drivingLimit.text = hmsTimeFormatter2(it)
        })

        viewModel.onEventWarningLiveData.observe(this, {
            Toast.makeText(
                requireContext(),
                "Warning!\n You continuously on duty more 14 hour",
                Toast.LENGTH_SHORT
            ).show()
        })

        viewModel.dutyCycleEventLiveData.observe(this, {
            Toast.makeText(
                requireContext(),
                "Warning!\n You on duty more 70 hour",
                Toast.LENGTH_SHORT
            ).show()
        })

        // observe event changes
        eventViewModel.eventList.observe(this, {
            if (it.isNotEmpty()) {
                viewModel.startCountDrivingEvent()
                viewModel.startCountOnEvent()
                viewModel.startCountDutyCycleEvent()
                viewModel.startCountDrivingLimit()
            }
        })
    }
}