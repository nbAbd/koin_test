package com.pieaksoft.event.consumer.android.ui.events

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentInsertEventBinding
import com.pieaksoft.event.consumer.android.enums.*
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.model.event.Location
import com.pieaksoft.event.consumer.android.model.event.isLocationSet
import com.pieaksoft.event.consumer.android.ui.profile.ProfileViewModel
import com.pieaksoft.event.consumer.android.utils.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class InsertEventFragment(
    private val event: Event?,
    private val onCancelled: (IsCancelled: Boolean) -> Unit
) : DialogFragment() {
    private var _binding: FragmentInsertEventBinding? = null
    private val binding get() = _binding!!
    private var isChecked = true

    private var eventModel =
        Event(
            eventType = EventInsertType.DUTY_STATUS_CHANGE.type,
            eventRecordOrigin = EventRecordOriginType.EDITED_OR_ENTERED_BY_THE_DRIVER.type,
            eventRecordStatus = EventRecordStatusType.ACTIVE.type,
            malfunctionIndicatorStatus = MalfunctionIndicatorStatusType.NO_ACTIVE_MALFUNCTION.type,
            dataDiagnosticEventIndicatorStatus = DataDiagnosticEventIndicatorStatusType.NO_ACTIVE_DATA_DIAGNOSTIC_EVENTS_FOR_DRIVER.type,
        )

    private val profileViewModel: ProfileViewModel by viewModel()

    private val timezone: Timezone? by lazy {
        Timezone.findByName(timezone = profileViewModel.getUserTimezone() ?: "")
    }

    private val viewModel: EventViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.ThemeDialog)
        LocationUtil.startGettingLocation(requireActivity(), requireContext()) {
            eventModel.coordinates = Location(it.latitude.toFloat(), it.longitude.toFloat())
            if (viewModel.isUserWaitingToSave()) binding.save.performClick()
        }
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
                return@observe
            }

            // If date is null set current date/time
            Date().apply {
                eventModel.date = formatToServerDateDefaults(timezone)
                eventModel.time = formatToServerTimeDefaults(timezone)
            }
        }

        viewModel.eventInsertCode.observe(viewLifecycleOwner) { eventCode ->
            eventCode?.let {
                when (eventCode) {
                    EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY -> {
                        binding.personalUserOrYardMvBtn.show()
                        binding.personalUseOrYardMv.text = getString(R.string.personal_use)
                    }

                    EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING -> {
                        binding.personalUserOrYardMvBtn.show()
                        binding.personalUseOrYardMv.text = getString(R.string.yard_mv)
                    }
                    else -> {
                    }
                }
                eventModel.eventCode = it.code
            }
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

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun setupView() = with(binding) {
        observe()

        // if user editing existing event
        event?.let {
            eventModel = event
            setData(event)
        }

        save.setOnClickListener {
            if (eventModel.isLocationSet()) {
                event?.let {
                    updateEvent()
                    return@setOnClickListener
                }
                insertEvent()
            } else viewModel.showProgress()
        }
        backInsertBtn.setOnClickListener {
            onCancelled(true)
            dialog?.dismiss()
        }
        personalUserOrYardMvBtn.setOnClickListener {
            personalUserOrYardMvBtn.switchSelectStopIcon(isChecked)
            if (isChecked) {
                eventModel.eventType =
                    EventInsertType.CHANGE_IN_DRIVERS_INDICATION_OF_AUTHORIZED_PERSONNEL_USE_OF_CMV_OR_YARD_MOVES.type

                if (personalUseOrYardMv.text.equals(getString(R.string.personal_use))) {
                    eventModel.eventCode =
                        EventCode.DRIVER_INDICATES_AUTHORIZED_PERSONAL_USE_OF_CMV.code
                } else {
                    eventModel.eventCode =
                        EventCode.DRIVER_INDICATES_YARD_MOVES.code
                }
            } else {
                eventModel.eventType = event?.eventType
                eventModel.eventCode = event?.eventCode
            }
            isChecked = !isChecked
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(event: Event) = with(binding) {
        event.run {
            dateTxt.show()
            eventStatus.show()
            dateTxt.editText.setText("$date $time")
            eventStatus.editText.setText(eventCode?.toReadable())
            locationDescription.editText.setText(driverLocationDescription)
            shipDocNumber.editText.setText(shippingDocumentNumber)
            to.editText.setText(toAddress)
            from.editText.setText(fromAddress)
            commentIt.editText.setText(comment)
            trailerNumber.editText.setText(trailer)
            odometer.editText.setText(totalEngineMiles.toString())
        }
    }

    private fun insertEvent() {
        mapViewData()
        viewModel.insertEvent(eventModel)
    }

    private fun updateEvent() = with(binding) {
        mapViewData()
        viewModel.updateEvent(eventModel)
    }

    private fun mapViewData() = with(binding) {
        eventModel.run {
            driverLocationDescription = locationDescription.editText.text.toString()
            shippingDocumentNumber = shipDocNumber.editText.text.toString()
            toAddress = to.editText.text.toString()
            fromAddress = from.editText.text.toString()
            comment = commentIt.editText.text.toString()
            val odometer = odometer.editText.text.toString()
            totalEngineMiles = if (odometer.isEmpty()) 0 else odometer.toInt()
            trailer = trailerNumber.editText.text.toString()
            eventRecordStatus = EventRecordStatusType.ACTIVE.type
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetInserting()
        _binding = null
    }
}