package com.pieaksoft.event.consumer.android.ui.appbar

import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentRulePageBinding
import com.pieaksoft.event.consumer.android.model.rules.RulesData
import com.pieaksoft.event.consumer.android.ui.appbar.adapter.RulesAdapter
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment

class RulePageFragment(private val rules: List<RulesData>) :
    BaseFragment<FragmentRulePageBinding>() {

    private val rulesAdapter: RulesAdapter by lazy {
        RulesAdapter()
    }

    override fun setupView() {
        rulesAdapter.rules = rules
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
            paint.color = ContextCompat.getColor(requireContext(), R.color.rules_separator)
        }

        decoration.setDrawable(divider)

        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = rulesAdapter
            addItemDecoration(decoration)
        }
    }
}