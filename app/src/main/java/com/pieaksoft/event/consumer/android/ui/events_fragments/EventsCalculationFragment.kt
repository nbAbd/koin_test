package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentHomeBinding
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment
import com.pieaksoft.event.consumer.android.utils.Storage
import com.pieaksoft.event.consumer.android.utils.hmsTimeFormatter
import org.koin.android.viewmodel.ext.android.viewModel

class EventsCalculationFragment : BaseFragment(R.layout.fragment_home) {
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val eventsCalculationVM: EventsCalculationVM by viewModel()

    override fun setViews() {
        eventsCalculationVM.calculateEvents()
        if (Storage.eventList.isNotEmpty()) {
            eventsCalculationVM.startCountDrivingEvent()
            eventsCalculationVM.startCountOnEvent()
            eventsCalculationVM.startCountDutyCycleEvent()
        }
    }

    override fun bindVM() {
        eventsCalculationVM.drivingEventLiveData.observe(this, {
            if(it < 0){
                binding.breakProgressBar.progressBarColor = ContextCompat.getColor(requireContext(),R.color.red)
            } else {
                binding.breakProgressBar.progressBarColor = ContextCompat.getColor(requireContext(),R.color.blue)
            }
            binding.breakInValue.text = hmsTimeFormatter(it)
            binding.breakProgressBar.progress =
                ((it.toFloat() / 60000 / on_Duty_Break_In_Minutes) * 100).toFloat()
        })

        eventsCalculationVM.onEventLiveData.observe(this, {
            if (it < 0) {
                Toast.makeText(
                    requireContext(),
                    "Warning!\n You continously onduty more 14 hour",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar2.progressBarColor = ContextCompat.getColor(requireContext(),R.color.red)
                binding.progressBar2.setProgressWithAnimation(100f, 1000)
            } else {
                binding.progressBar2.progressBarColor = ContextCompat.getColor(requireContext(),R.color.blue)
                binding.progressBar2.progress =
                    ((it.toFloat() / 60000 / on_Duty_Window_Minutes) * 100).toFloat()
            }
            binding.onValue.text = hmsTimeFormatter(it)
        })

        eventsCalculationVM.dutyCycleEventLiveData.observe(this, {
            if (it < 0) {
                Toast.makeText(
                    requireContext(),
                    "Warning!\n You onduty more 70 hour",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar3.progressBarColor = ContextCompat.getColor(requireContext(),R.color.blue)
                binding.progressBar3.progressBarColor = ContextCompat.getColor(requireContext(),R.color.red)
                binding.progressBar3.setProgressWithAnimation(100f, 1000)
            } else {
                binding.progressBar3.progressBarColor = ContextCompat.getColor(requireContext(),R.color.blue)

                binding.progressBar3.progress =
                    ((it.toFloat() / 60000 / on_Duty_Cycle_Minutes) * 100).toFloat()
            }
            binding.dutyCycle.text = hmsTimeFormatter(it)
        })

        eventsCalculationVM.onEventWarningLiveData.observe(this, {
            Toast.makeText(
                requireContext(),
                "Warning!\n You continously onduty more 14 hour",
                Toast.LENGTH_SHORT
            ).show()
        })

        eventsCalculationVM.dutyCycleEventLiveData.observe(this, {
            Toast.makeText(
                requireContext(),
                "Warning!\n You onduty more 70 hour",
                Toast.LENGTH_SHORT
            ).show()
        })
    }
}