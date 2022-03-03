package com.pieaksoft.event.consumer.android.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.DialogInsertEventBinding
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.utils.hideSystemUI

class InsertEventDialog(private val action: (InsertEventDialog, EventCode) -> Unit) :
    DialogFragment() {
    private var _binding: DialogInsertEventBinding? = null
    private val binding get() = _binding!!

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
        hideSystemUI()
        _binding = DialogInsertEventBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.ThemeDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        isCancelable = false
        binding.apply {
            eventOffBtn.setOnClickListener(eventButtonListener)
            eventOnBtn.setOnClickListener(eventButtonListener)
            eventSleepBtn.setOnClickListener(eventButtonListener)
            eventDrivingBtn.setOnClickListener(eventButtonListener)
            eventCancelBtn.setOnClickListener(eventButtonListener)
            eventCancelBtn.setOnClickListener(eventButtonListener)
        }
    }

    private val eventButtonListener = View.OnClickListener {
        when (it.id) {
            R.id.event_off_btn -> action.invoke(
                this,
                EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY
            )
            R.id.event_on_btn -> action.invoke(
                this,
                EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING
            )
            R.id.event_sleep_btn -> action.invoke(
                this,
                EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_SLEEPER_BERTH
            )
            R.id.event_driving_btn -> action.invoke(
                this,
                EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING
            )
            R.id.event_cancel_btn -> dialog?.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}