package com.pieaksoft.event.consumer.android.ui.events

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentInsertEventBinding
import com.pieaksoft.event.consumer.android.enums.*
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.model.event.Location
import com.pieaksoft.event.consumer.android.model.event.isLocationSet
import com.pieaksoft.event.consumer.android.ui.profile.ProfileViewModel
import com.pieaksoft.event.consumer.android.utils.*
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class InsertEventFragment(
    private val event: Event?,
    private val onCancelled: (Boolean) -> Unit
) : DialogFragment() {
    private var _binding: FragmentInsertEventBinding? = null
    private val binding get() = _binding!!
    private var isChecked = true
    private var isAllowedPc = false
    private var isAllowedYm = false


    private var eventModel =
        Event(
            eventType = EventInsertType.DUTY_STATUS_CHANGE.type,
            eventRecordOrigin = EventRecordOriginType.EDITED_OR_ENTERED_BY_THE_DRIVER.type,
            eventRecordStatus = EventRecordStatusType.ACTIVE.type,
            malfunctionIndicatorStatus = MalfunctionIndicatorStatusType.NO_ACTIVE_MALFUNCTION.type,
            dataDiagnosticEventIndicatorStatus = DataDiagnosticEventIndicatorStatusType.NO_ACTIVE_DATA_DIAGNOSTIC_EVENTS_FOR_DRIVER.type,
        )

    private val viewModel: EventViewModel by sharedViewModel()
    private val profileViewModel: ProfileViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.ThemeDialog)
        LocationUtil.startGettingLocation(requireActivity(), requireContext()) {
            eventModel.coordinates = Location(it.latitude.toFloat(), it.longitude.toFloat())
            if (isFragmentVisible() && viewModel.isUserWaitingToSave()) {
                binding.save.performClick()
            }
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

    private fun setupView() = with(binding) {
        observe()
        checkPersonalUseAndYardMove()

        // if user editing existing event
        event?.let {
            eventModel = event
            fillUI(event)
        }

        save.setOnClickListener {
            // Update (If local event is not null, then it's update)
            if (event != null) {
                updateEvent()
                return@setOnClickListener
            }

            // Insert
            if (eventModel.isLocationSet()) insertEvent()
            else viewModel.showProgress()
        }

        backInsertBtn.setOnClickListener {
            onCancelled(true)
            dialog?.dismiss()
        }

        personalUserOrYardMvBtn.setOnClickListener {
            if (isChecked) {
                eventModel.eventType =
                    EventInsertType.CHANGE_IN_DRIVERS_INDICATION_OF_AUTHORIZED_PERSONNEL_USE_OF_CMV_OR_YARD_MOVES.type

                when (EventCode.findByCode(eventModel.eventCode ?: "")) {
                    EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY -> {
                        if (!isAllowedPc) {
                            toast(getString(R.string.pc_unavailable))
                            return@setOnClickListener
                        }
                        eventModel.eventCode =
                            EventCode.DRIVER_INDICATES_AUTHORIZED_PERSONAL_USE_OF_CMV.code
                    }
                    EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING -> {
                        if (!isAllowedYm) {
                            toast(getString(R.string.ym_unavailable))
                            return@setOnClickListener
                        }
                        eventModel.eventCode = EventCode.DRIVER_INDICATES_YARD_MOVES.code
                    }
                    else -> Unit
                }
            }
            isChecked = !isChecked
            personalUserOrYardMvBtn.switchSelectStopIcon(isChecked)
        }
    }

    private fun observe() {
        KeyboardHeightProvider(
            requireContext(),
            requireActivity().windowManager,
            binding.root
        ) { keyboardHeight, isOpen ->
            if (isOpen) {
                binding.scrollContainer.updatePadding(bottom = keyboardHeight)
            } else {
                binding.scrollContainer.updatePadding(bottom = 0)
            }
        }

        viewModel.eventInsertDate.observe(viewLifecycleOwner) breaking@{
            if (event != null) return@breaking

            // If date is not null, then set date/time
            it?.let {
                eventModel.date = it.formatToServerDateDefaults()
                eventModel.time = it.formatToServerTimeDefaults()
                return@breaking
            }

            // If date is null set current date/time
            eventModel.date = viewModel.getFormattedUserDate()
            eventModel.time = viewModel.getFormattedUserTime()
        }

        viewModel.eventInsertCode.observe(viewLifecycleOwner) { eventCode ->
            eventCode?.let {
                when (eventCode) {
                    EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY -> with(binding) {
                        personalUserOrYardMvBtn.show()
                        personalUseOrYardMv.text = getString(R.string.personal_use)
                    }

                    EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING -> with(binding) {
                        personalUserOrYardMvBtn.show()
                        personalUseOrYardMv.text = getString(R.string.yard_mv)
                    }
                    else -> Unit
                }
                eventModel.eventCode = it.code
            }
        }

        viewModel.event.observe(this) {
            it?.let {
                viewModel.getEventList()
                viewModel.checkForIntermediateLog(it, requireActivity())
                dialog?.dismiss()
            }
        }

        viewModel.localEvent.observe(this) {
            it?.let {
                viewModel.getEventList()
                viewModel.checkForIntermediateLog(it, requireActivity())
                dialog?.dismiss()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fillUI(event: Event) = with(binding) {
        with(event) {
            dateTxt.show()
            dateTxt.editText.alpha = 0.5F
            eventStatus.editText.alpha = 0.5F
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
        updateEventModel()
        lifecycleScope.launch {
            eventModel.coDriverId = profileViewModel.getCoDriverId()
            viewModel.insertEvent(eventModel)
        }
    }

    private fun updateEvent() {
        updateEventModel()
        viewModel.updateEvent(eventModel)
    }

    private fun updateEventModel() = with(binding) {
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

    private fun checkPersonalUseAndYardMove() {
        lifecycleScope.launch {
            isAllowedPc = profileViewModel.isPersonalUseAllowed() == true
            isAllowedYm = profileViewModel.isYardMoveAllowed() == true
        }
    }
}