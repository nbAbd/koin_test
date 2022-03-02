package com.pieaksoft.event.consumer.android.ui.appbar

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentRulesBinding
import com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter.UsaRulesAdapter
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment

class RulesFragment : BaseFragment<FragmentRulesBinding>() {
    private val ruleData: Map<String, String> by lazy {
        mapOf(
            "Available driving" to "08:00",
            "Rest break" to "08:00",
            "Driving" to "11:00",
            "On-Duty" to "14:00",
            "Weekly" to "70:00",
            "Shift hours" to "----",
            "Day off remaining" to "----",
        )
    }
    private val usaRulesAdapter: UsaRulesAdapter by lazy { UsaRulesAdapter() }

    override fun setupView() {
        usaRulesAdapter.rules = ruleData

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
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
            }.also {
                it.drawable?.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    ), PorterDuff.Mode.SRC_IN
                )
            }
            addItemDecoration(decoration)
            adapter = usaRulesAdapter
        }

        binding.btnClose.setOnClickListener { findNavController().popBackStack() }
    }
}