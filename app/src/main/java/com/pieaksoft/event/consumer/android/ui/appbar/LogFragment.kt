package com.pieaksoft.event.consumer.android.ui.appbar

import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentLogBinding
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.EditEvent
import com.pieaksoft.event.consumer.android.model.EventInsertCode
import com.pieaksoft.event.consumer.android.ui.activities.main.IMainAction
import com.pieaksoft.event.consumer.android.ui.activities.main.MainActivity
import com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter.EventListAdapter
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
    init {
        requiresBottomNavigation = false
    }

    override val viewModel: EventViewModel by sharedViewModel()

    private val eventsAdapter by lazy { EventsAdapter() }

    private val eventListAdapter by lazy {
        EventListAdapter { event ->
            viewModel.setEventInsertCode(code = EventInsertCode.getByCode(event.eventCode ?: ""))
            viewModel.setEventInsertDate(date = event.certifyDate?.first()?.date?.getDateFromString()!!)
            showInsertEventPagerDialog()
        }
    }

    private val eventList = mutableListOf<EditEvent>().apply {
        add(EditEvent.Header())
    }

    private var sliderPosition: Int = 7
        set(value) {
            field = value
            eventList.removeAll { it is EditEvent.Content }
            eventListAdapter.list = eventList.apply {
                viewModel.getEventsGroupByDate().values.toList()[value].forEach { event ->
                    add(EditEvent.Content(event = event))
                }
            }
        }

    override fun onStart() {
        super.onStart()
        (activity as IMainAction).hideBottomNavigation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getEventsGroupByDate().values.toList().apply {
            if (isNotEmpty() && last().isNotEmpty()) {
                last().forEach {
                    eventList.add(EditEvent.Content(event = it))
                }
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupView() {
        binding.apply {
            closeLog.setOnClickListener {
                findNavController().popBackStack()
            }

            insertBtn.setOnClickListener {
                showInsertEventDialog()
            }

            nextBtn.setOnClickListener { openNext() }

            prevBtn.setOnClickListener { goBack() }

            viewModel.getEventsGroupByDate().values.toList().apply {
                listOfEventsBtn.isEnabled = isNotEmpty() && last().isNotEmpty()
            }

            listOfEventsBtn.setOnClickListener {
                handleEventsListButtonClick()
            }
        }
        setupRecyclerview()
    }

    private fun handleEventsListButtonClick() = with(binding) {
        if (eventsListRecyclerview.isGone) {
            eventsRecyclerview.hideWithAnimation(to = Gravity.TOP, 500)
            eventsListRecyclerview.showWithAnimation(500)
            listOfEventsBtn.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
        } else {
            eventsListRecyclerview.hideWithAnimation(to = Gravity.BOTTOM, 500)
            eventsRecyclerview.showWithAnimation(500)
            listOfEventsBtn.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.selector_button_blue)
            )
        }
    }

    private fun setupRecyclerview() {
        binding.eventsRecyclerview.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    .apply {
                        stackFromEnd = true
                    }
            adapter = eventsAdapter
            attachSnapHelperWithListener(
                PagerSnapHelper(),
                SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
                object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        sliderPosition = position
                        launch {
                            binding.listOfEventsBtn.isEnabled =
                                viewModel.getEventsGroupByDate().values.toList()[position].isNotEmpty()
                            binding.nextBtn.isEnabled = position != eventsAdapter.list.size.minus(1)
                            binding.prevBtn.isEnabled = position != 0
                            binding.dateText.text =
                                eventsAdapter.list.keys.elementAt(sliderPosition)
                                    .getDateFromString()
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

        eventListAdapter.list = eventList

        val decoration = object : DividerItemDecoration(requireContext(), VERTICAL) {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val itemPosition = parent.getChildAdapterPosition(view)
                // hide divider for the last child
                if (itemPosition == state.itemCount.minus(1)) {
                    outRect.setEmpty()
                } else {
                    super.getItemOffsets(outRect, view, parent, state)
                }
            }
        }

        val divider = ShapeDrawable(RectShape()).apply {
            intrinsicHeight = 2
            paint.color = ContextCompat.getColor(requireContext(), R.color.separator)
        }

        decoration.setDrawable(divider)

        binding.eventsListRecyclerview.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = eventListAdapter
            addItemDecoration(decoration)
        }
    }

    private fun setEvents() {
        eventsAdapter.list =
            viewModel.getEventsGroupByDate()
        binding.dateText.text = eventsAdapter.list.keys.last().getDateFromString()
            .formatToServerDateDefaults2()
    }

    override fun observe() {
        viewModel.eventList.observe(this, {
            setEvents()
        })

        viewModel.eventListByDate.observe(this, {
            setEvents()
        })

        viewModel.progress.observe(this, {
            (requireActivity() as MainActivity).setProgressVisible(it)
        })

        viewModel.error.observe(this, {
            Log.e("test_logger_error", "test insert error response = ${it.message}")
        })
    }

    private fun openNext() {
        if (sliderPosition == eventsAdapter.list.size.minus(1)) return
        binding.eventsRecyclerview.smoothScrollToPosition(sliderPosition.plus(1))
    }

    private fun goBack() {
        if (sliderPosition == 0) return
        binding.eventsRecyclerview.smoothScrollToPosition(sliderPosition.minus(1))
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
        val dialog = InsertEventPagerDialog {
            (requireActivity() as MainActivity).binding.bottomNavigation.hide()
        }
        dialog.show(childFragmentManager, InsertEventPagerDialog::class.java.name)
    }
}