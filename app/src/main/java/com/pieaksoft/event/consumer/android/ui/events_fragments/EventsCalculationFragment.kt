package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.util.Log
import by.kirich1409.viewbindingdelegate.viewBinding
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentCoDriverBinding
import com.pieaksoft.event.consumer.android.databinding.FragmentHomeBinding
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment
import com.pieaksoft.event.consumer.android.ui.profile.ProfileVM
import com.pieaksoft.event.consumer.android.utils.hmsTimeFormatter
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.DecimalFormat

class EventsCalculationFragment: BaseFragment(R.layout.fragment_home) {
    private val binding by viewBinding(FragmentHomeBinding::bind)
     private val eventsCalculationVM: EventsCalculationVM by viewModel()

     override fun setViews() {
         eventsCalculationVM.initBreakIn()
         eventsCalculationVM.startCountBreakIn()

     }

     override fun bindVM() {
        eventsCalculationVM.breakInEventLiveData.observe(this, {
            binding.breakInValue.text = hmsTimeFormatter(it.remainMillis)
            binding.breakProgressBar.progress = ((it.remainMillis.toFloat()/it.totalLimit.toFloat()) * 100).toFloat()
        })
     }
 }