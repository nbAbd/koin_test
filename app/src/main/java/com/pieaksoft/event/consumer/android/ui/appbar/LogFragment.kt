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
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentLogBinding
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.event.edit.EditEvent
import com.pieaksoft.event.consumer.android.ui.activities.main.IMainAction
import com.pieaksoft.event.consumer.android.ui.activities.main.MainActivity
import com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter.EventListAdapter
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import com.pieaksoft.event.consumer.android.ui.dialog.InsertEventDialog
import com.pieaksoft.event.consumer.android.utils.*
import com.pieaksoft.event.consumer.android.utils.graph.EventDataSet
import com.pieaksoft.event.consumer.android.utils.graph.GraphManager
import com.pieaksoft.event.consumer.android.views.Dialogs
import com.pieaksoft.event.consumer.android.views.Dialogs.showInsertEventDialogFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel
import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.pieaksoft.event.consumer.android.enums.EventRecordOriginType
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.model.event.getStartTime
import com.pieaksoft.event.consumer.android.ui.profile.ProfileViewModel
import com.pieaksoft.event.consumer.android.utils.graph.YAxisRenderer
import com.pieaksoft.event.consumer.android.utils.graph.yAxis

class LogFragment : BaseMVVMFragment<FragmentLogBinding, EventViewModel>() {
    init {
        requiresBottomNavigation = false
    }

    override val viewModel: EventViewModel by sharedViewModel()
    private val profileViewModel: ProfileViewModel by sharedViewModel()

    private val eventListAdapter by lazy {
        EventListAdapter { event ->
            if (event.eventRecordOrigin == EventRecordOriginType.EDITED_OR_ENTERED_BY_THE_DRIVER.type) {
                event.getStartTime()?.let {
                    showInsertEventDialogFragment(childFragmentManager, it)
                }
            } else toast(getString(R.string.no_access_to_edit_autorecorded_event))
        }
    }

    private val eventList = mutableListOf<EditEvent>().apply {
        add(EditEvent.Header())
    }

    private var sliderPosition: Int = 0

    override fun onStart() {
        super.onStart()
        (activity as IMainAction).hideBottomNavigation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        setupRecyclerview()
    }

    override fun setupView() {
        profileViewModel.getProfile(fromDB = true)
        binding.apply {
            closeLog.setOnClickListener { findNavController().popBackStack() }

            insertBtn.setOnClickListener { showInsertEventDialog() }

            nextBtn.setOnClickListener { openNext() }

            prevBtn.setOnClickListener { goBack() }

            listOfEventsBtn.setOnClickListener { handleEventsListButtonClick() }
        }
    }

    private fun handleEventsListButtonClick() = with(binding) {
        if (eventsListRecyclerview.isGone) {
            lineChart.hideWithAnimation(to = Gravity.TOP, 500)
            eventsListRecyclerview.showWithAnimation(500)
            listOfEventsBtn.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
        } else {
            eventsListRecyclerview.hideWithAnimation(to = Gravity.BOTTOM, 500)
            lineChart.showWithAnimation(500)
            listOfEventsBtn.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.selector_button_blue)
            )
        }
    }

    private fun setupRecyclerview() {
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
        sliderPosition = viewModel.getEventsGroupByDate().toList().lastIndex
        // Initial update of UI
        updateUI()
    }

    override fun observe() {
        viewModel.eventList.observe(this) {
            setEvents()
        }

        profileViewModel.currentDriverProfile.observe(viewLifecycleOwner) {
            eventListAdapter.truckName = it.vehicle?.name
        }
        viewModel.eventListByDate.observe(this) {
            setEvents()
        }

        viewModel.progress.observe(this) {
            (requireActivity() as MainActivity).setProgressVisible(it)
        }

        viewModel.error.observe(this) {
            Log.e("test_logger_error", "test insert error response = ${it.message}")
        }
    }

    private fun openNext() {
        if (sliderPosition == viewModel.getEventsGroupByDate().toList().lastIndex) {
            binding.nextBtn.isEnabled = false
            return
        }

        sliderPosition += 1

        binding.prevBtn.isEnabled = true

        // Update UI if sliderPosition is not at lastIndex
        updateUI()

        // Check button state again
        binding.nextBtn.isEnabled =
            sliderPosition != viewModel.getEventsGroupByDate().toList().lastIndex
    }

    private fun goBack() {
        if (sliderPosition == 0) {
            binding.prevBtn.isEnabled = false
            return
        }

        sliderPosition -= 1
        binding.nextBtn.isEnabled = true

        // Update UI if sliderPosition is not initial
        updateUI()

        binding.prevBtn.isEnabled = sliderPosition > 0
    }

    private fun updateUI() = with(viewModel.getEventsGroupByDate()) {
        val dates = keys
        val events = values.toList()

        events.elementAtOrNull(sliderPosition).also {
            // Draw chart
            drawChart(it?.toMutableList() ?: mutableListOf())

            //  Clear event list, setup for current sliderPos date
            eventList.removeAll { editEvent -> editEvent is EditEvent.Content }
            it?.map { event -> EditEvent.Content(event) }?.toCollection(eventList)
            eventListAdapter.list = eventList

            // Disable/enable eventListButton
            binding.listOfEventsBtn.isEnabled =
                eventList.filterIsInstance<EditEvent.Content>().isNotEmpty()
        }

        dates.elementAtOrNull(sliderPosition)?.getDateFromString()?.formatToServerDateDefaults2()
            .also { binding.dateText.text = it }
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
        Dialogs.showDateTimeSelector(requireContext(), sp = viewModel.sp) { date ->
            viewModel.setEventInsertDate(date = date)
            showInsertEventDialogFragment(childFragmentManager)
        }
    }

    private fun drawChart(events: MutableList<Event>) {
        val eventDataSet = EventDataSet(events = events).apply {
            axisDependency = YAxis.AxisDependency.LEFT
            lineWidth = 2.2F
            colors = getColorsFor(entries = values)
            mode = LineDataSet.Mode.STEPPED
            setDrawCircles(false)
            setDrawValues(false)
        }

        binding.lineChart.apply {
            updateRightAxisLabels(events)
            data = LineData(eventDataSet)
            notifyDataSetChanged()
            animateX(1000)
        }
    }

    private fun setupChart() = with(binding) {
        // Setup line chart
        lineChart.apply {
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false
            isClickable = false
            rendererLeftYAxis = YAxisRenderer(
                viewPortHandler,
                axisLeft,
                getTransformer(YAxis.AxisDependency.LEFT),
                requireContext()
            )
            rendererRightYAxis = YAxisRenderer(
                viewPortHandler,
                axisRight,
                getTransformer(YAxis.AxisDependency.RIGHT),
                requireContext()
            )
            setDrawGridBackground(false)
            setDrawBorders(true)
            setBorderColor(ContextCompat.getColor(requireContext(), R.color.separator))
            setBackgroundColor(Color.TRANSPARENT)
            setNoDataTextColor(Color.TRANSPARENT)
            setTouchEnabled(false)
        }

        setupXAxis()
        setupYAxisLeft()
        setupYAxisRight()
    }

    private fun setupXAxis() = with(binding.lineChart.xAxis) {
        position = XAxis.XAxisPosition.TOP
        yOffset = 4F
        axisMinimum = 0f
        axisMaximum = 24f
        labelCount = 25
        textColor = ContextCompat.getColor(requireContext(), R.color.secondary_gray)
        textSize = 12F
        setCenterAxisLabels(false)
        setDrawGridLines(false)
        setDrawAxisLine(false)
        setDrawGridLinesBehindData(false)
        setDrawLimitLinesBehindData(false)

        valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                if (value == -1f || value >= GraphManager.xAxisLabels.size) return ""
                return GraphManager.xAxisLabels[value.toInt()]
            }
        }
    }


    private fun setupYAxisLeft() = with(binding.lineChart.axisLeft) {
        axisMinimum = 0f
        axisMaximum = 5f
        xOffset = 16F
        gridColor = ContextCompat.getColor(requireContext(), R.color.separator)
        textSize = 14F
        typeface = Typeface.DEFAULT_BOLD
        setLabelCount(6, true)
        setCenterAxisLabels(true)
        setDrawGridLines(true)
        setDrawAxisLine(false)
        enableGridDashedLine(10F, 8F, 0F)

        valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                if (value == -1f || value >= GraphManager.yAxisLabels.size) return ""
                return GraphManager.yAxisLabels[value.toInt()]
            }
        }
    }

    private fun setupYAxisRight() = with(binding.lineChart.axisRight) {
        axisMinimum = 0f
        axisMaximum = 5f
        xOffset = 16F
        gridColor = ContextCompat.getColor(requireContext(), R.color.separator)
        textSize = 14F
        setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        setLabelCount(6, true)
        setCenterAxisLabels(true)
        setDrawGridLines(false)
        setDrawAxisLine(false)
        setDrawZeroLine(false)
        setDrawTopYLabelEntry(false)

        valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val valueTotal = axis!!.mCenteredEntries.sum().toString()
                val hm = valueTotal.split(".")
                val hour = hm.first()
                val min = hm.last().mapIndexed { index, c ->
                    if (index == 0 && c == '0') return "" else c
                }.toString()

                val normalMinute = (min.toFloat() / 100).times(60).toInt()
                val formattedMinute = if (normalMinute < 10) "0$normalMinute" else "$normalMinute"

                if (axis.mEntryCount == 0) return ""
                return "$hour:$formattedMinute"
            }
        }
    }

    private fun updateRightAxisLabels(events: List<Event>) {
        binding.lineChart.axisRight.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                if (value == 0F) return ""

                val eventsByYAxis = events.groupBy { it.yAxis() }
                val eventsByCurrentAxisValue = eventsByYAxis.getOrElse(value) { emptyList() }
                eventsByCurrentAxisValue.forEach {
                    it.endTime?.let { endTime ->
                        if (endTime.contentEquals("25:00")) {
                            it.endTime = "24:00"
                        }
                    }
                    it.calculateDuration()
                }
                val totalDuration = eventsByCurrentAxisValue.sumOf { it.durationInMillis }
                return hmTimeFormatter(totalDuration)
            }
        }
    }


    private fun getColorsFor(entries: List<Entry>): List<Int> {
        val colors = mutableListOf<Int>()
        entries.forEachIndexed { index, entry ->
            val nextEntry = entries.elementAtOrNull(index.inc())

            when (entry.y) {
                1F -> {
                    if (nextEntry != null) {
                        if (entry.y < nextEntry.y && entry.x == nextEntry.x) {
                            colors.add(ContextCompat.getColor(context!!, R.color.separator))
                        } else {
                            colors.add(ContextCompat.getColor(context!!, R.color.toast_yellow))
                        }
                    } else {
                        colors.add(ContextCompat.getColor(context!!, R.color.toast_yellow))
                    }
                }
                2F -> {
                    if (nextEntry != null) {
                        if (entry.y < nextEntry.y && entry.x == nextEntry.x) {
                            colors.add(ContextCompat.getColor(context!!, R.color.separator))
                        } else if (entry.y > nextEntry.y && entry.x == nextEntry.x) {
                            colors.add(ContextCompat.getColor(context!!, R.color.separator))
                        } else {
                            colors.add(ContextCompat.getColor(context!!, R.color.toast_green))
                        }
                    } else {
                        colors.add(ContextCompat.getColor(context!!, R.color.toast_green))
                    }
                }
                3F -> {
                    if (nextEntry != null) {
                        if (entry.y < nextEntry.y && entry.x == nextEntry.x) {
                            colors.add(ContextCompat.getColor(context!!, R.color.separator))
                        } else if (entry.y > nextEntry.y && entry.x == nextEntry.x) {
                            colors.add(ContextCompat.getColor(context!!, R.color.separator))
                        } else {
                            colors.add(ContextCompat.getColor(context!!, R.color.toast_blue))
                        }
                    } else {
                        colors.add(ContextCompat.getColor(context!!, R.color.toast_blue))
                    }
                }
                4F -> {
                    if (nextEntry != null) {
                        if (entry.y > nextEntry.y && entry.x == nextEntry.x) {
                            colors.add(ContextCompat.getColor(context!!, R.color.separator))
                        } else {
                            colors.add(ContextCompat.getColor(context!!, R.color.toast_red))
                        }
                    } else {
                        colors.add(ContextCompat.getColor(context!!, R.color.toast_red))
                    }
                }
            }
        }
        return colors
    }
}