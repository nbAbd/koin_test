package com.pieaksoft.event.consumer.android.ui.events_fragments

import by.kirich1409.viewbindingdelegate.viewBinding
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentHomeBinding
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment
import com.pieaksoft.event.consumer.android.utils.hmsTimeFormatter
import org.koin.android.viewmodel.ext.android.viewModel

class EventsCalculationFragment: BaseFragment(R.layout.fragment_home) {
     private val binding by viewBinding(FragmentHomeBinding::bind)
     private val eventsCalculationVM: EventsCalculationVM by viewModel()

     override fun setViews() {
         eventsCalculationVM.calculateEvents()
         eventsCalculationVM.startCountDrivingEvent()
         eventsCalculationVM.startCountOnEvent()
     }

     override fun bindVM() {
        eventsCalculationVM.drivingEventLiveData.observe(this, {
            binding.breakInValue.text = hmsTimeFormatter(it)
            binding.breakProgressBar.progress = ((it.toFloat()/60000/on_Duty_Break_In_Minutes) * 100).toFloat()
        })

         eventsCalculationVM.onEventEventLiveData.observe(this, {
            binding.onValue.text = hmsTimeFormatter(it)
            binding.progressBar2.progress = ((it.toFloat()/60000/on_Duty_Window_Minutes) * 100).toFloat()
        })
     }
 }