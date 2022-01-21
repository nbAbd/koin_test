package com.pieaksoft.event.consumer.android.ui.appbar

import android.util.Log
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pieaksoft.event.consumer.android.databinding.FragmentLogBinding
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.network.ErrorHandler
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import com.pieaksoft.event.consumer.android.ui.dialog.InsertEventDialog
import com.pieaksoft.event.consumer.android.ui.events.InsertEventPagerDialog
import com.pieaksoft.event.consumer.android.ui.events.adapter.EventsAdapter
import com.pieaksoft.event.consumer.android.utils.*
import com.pieaksoft.event.consumer.android.views.Dialogs
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

class LogFragment : BaseMVVMFragment<FragmentLogBinding, EventViewModel>() {
    override val viewModel: EventViewModel by sharedViewModel()

    private val eventsAdapter by lazy { EventsAdapter() }
    private var sliderPosition: Int = 0


    override fun setupView() {
        binding.apply {
            closeLog.setOnClickListener {
                findNavController().popBackStack()
            }

            insertBtn.setOnClickListener {
                showInsertEventDialog()
            }
        }
        setupRecyclerview()
    }

    private fun setupRecyclerview() {
        binding.eventsRecyclerview.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = eventsAdapter
            attachSnapHelperWithListener(
                PagerSnapHelper(),
                SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
                object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        sliderPosition = position
                        launch {
                            binding.dateText.text =
                                eventsAdapter.list.keys.elementAt(position).getDateFromString()
                                    .formatToServerDateDefaults2()
                        }
                    }

                    override fun onSnapPositionDragging() = Unit

                    override fun onSnapPositionNotChange(position: Int) {
                        launch {
                            if (position > 0) {
                                binding.dateText.text =
                                    eventsAdapter.list.keys.elementAt(position).getDateFromString()
                                        .formatToServerDateDefaults2()
                            }
                        }
                    }

                })
        }
    }

    private fun setEvents() {
        eventsAdapter.list = viewModel.getEventsGroupByDate()
        eventsAdapter.notifyDataSetChanged()

        binding.dateText.text = eventsAdapter.list.keys.elementAtOrNull(0)?.getDateFromString()
            ?.formatToServerDateDefaults2() ?: ""
    }

    override fun observe() {
        viewModel.eventList.observe(this, {
            setEvents()
        })

        viewModel.eventListByDate.observe(this, {
            setEvents()
        })

        viewModel.progress.observe(this, {
            // setProgressVisible(it)
        })

        viewModel.error.observe(this, {
            val error = ErrorHandler.getErrorMessage(it, requireContext())
            Log.e("test_logerrror", "test insert error response = $error")
        })
    }

    private fun showInsertEventDialog() {
        val dialog = InsertEventDialog { dialog, eventCode ->
            viewModel.setEventInsertCode(code = eventCode)
            dialog.dismiss()
            selectDate()
        }
        dialog.dialog?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )

        dialog.show(childFragmentManager, InsertEventDialog::class.java.name)
    }

    private fun selectDate() {
        Dialogs.showDateTimeSelector(
            requireContext(),
            object : Dialogs.DateSelectListener {
                override fun onDateSelect(date: Date) {
                    viewModel.setEventInsertDate(date = date)
                    showInsertEventPagerDialog()
                }
            })
    }

    private fun showInsertEventPagerDialog() {
        val dialog = InsertEventPagerDialog()
        dialog.show(childFragmentManager, InsertEventPagerDialog::class.java.name)
    }
}