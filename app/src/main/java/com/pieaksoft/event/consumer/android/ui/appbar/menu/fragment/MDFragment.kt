package com.pieaksoft.event.consumer.android.ui.appbar.menu.fragment

import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentMdBinding
import com.pieaksoft.event.consumer.android.model.md.DataGenerator
import com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter.DiagnosticsAdapter
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment

class MDFragment : BaseFragment<FragmentMdBinding>() {
    init {
        requiresBottomNavigation = false
    }

    private val diagnosticsAdapter by lazy { DiagnosticsAdapter() }

    override fun setupView() {
        diagnosticsAdapter.diagnostics = DataGenerator.createMD()

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
                val drawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.menu_item_separator)
                drawable.let { d -> it.setDrawable(d!!) }
            }
            addItemDecoration(decoration)
            adapter = diagnosticsAdapter
        }
    }
}