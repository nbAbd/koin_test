package com.pieaksoft.event.consumer.android.ui.events

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentInsertEventBinding
import com.pieaksoft.event.consumer.android.enums.EventInsertType
import com.pieaksoft.event.consumer.android.enums.Timezone
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.model.event.Location
import com.pieaksoft.event.consumer.android.ui.profile.ProfileViewModel
import com.pieaksoft.event.consumer.android.utils.LocationUtil
import com.pieaksoft.event.consumer.android.utils.formatToServerDateDefaults
import com.pieaksoft.event.consumer.android.utils.formatToServerTimeDefaults
import com.pieaksoft.event.consumer.android.utils.switchSelectStopIcon
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class InsertEventFragment(
    private val onCancelled: (IsCancelled: Boolean) -> Unit
) : DialogFragment() {
    private var _binding: FragmentInsertEventBinding? = null
    private val binding get() = _binding!!
    private var isChecked = true

    private val eventModel: Event by lazy {
        Event(
            "",
            eventType = EventInsertType.DUTY_STATUS_CHANGE.type,
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

    private val profileViewModel: ProfileViewModel by viewModel()

    private val timezone: Timezone? by lazy {
        Timezone.findByName(timezone = profileViewModel.getUserTimezone() ?: "")
    }

    private val viewModel: EventViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.ThemeDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.decorView?.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    private fun observe() {
        viewModel.eventInsertDate.observe(viewLifecycleOwner) {
            // If date is not null, then set date/time
            it?.let {
                eventModel.date = it.formatToServerDateDefaults(timezone)
                eventModel.time = it.formatToServerTimeDefaults(timezone)
            }

            // If date is null set current date/time
            Date().apply {
                eventModel.date = formatToServerDateDefaults(timezone)
                eventModel.time = formatToServerTimeDefaults(timezone)
            }
        }

        viewModel.eventInsertCode.observe(viewLifecycleOwner) { eventCode ->
            eventCode?.let { eventModel.eventCode = it.code }
        }

        viewModel.event.observe(this) {
            it?.let {
                viewModel.getEventList()
                dialog?.dismiss()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsertEventBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupView() = with(binding) {
        observe()
        save.setOnClickListener {
            LocationUtil.getCurrentLocationOnce(requireActivity(), requireContext()) {
                eventModel.apply {
                    coordinates = Location(
                        it.latitude.toFloat(),
                        it.longitude.toFloat()
                    )
                }
                viewModel.insertEvent(eventModel)
            }
        }
        backInsertBtn.setOnClickListener {
            onCancelled(true)
            dialog?.dismiss()
        }
        personalUse.setOnClickListener {
            personalUse.switchSelectStopIcon(isChecked)
            isChecked = !isChecked
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetInserting()
        _binding = null
    }
}