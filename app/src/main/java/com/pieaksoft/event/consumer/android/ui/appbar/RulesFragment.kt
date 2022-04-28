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
import com.pieaksoft.event.consumer.android.model.rules.Rules
import com.pieaksoft.event.consumer.android.model.rules.RulesData
import com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter.RulesAdapter
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment

class RulesFragment : BaseFragment<FragmentRulesBinding>() {
    private val usaRulesList = mutableListOf<RulesData>().apply {
        add(RulesData.Header())
        add(RulesData.Content(Rules("Available driving", "08:00")))
        add(RulesData.Content(Rules("Rest break", "08:00")))
        add(RulesData.Content(Rules("Driving", "08:00")))
        add(RulesData.Content(Rules("On-Duty", "08:00")))
        add(RulesData.Content(Rules("Weekly", "08:00")))
    }
    private val usaRulesAdapter: RulesAdapter by lazy { RulesAdapter() }

    override fun setupView() {
        usaRulesAdapter.rules = usaRulesList

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