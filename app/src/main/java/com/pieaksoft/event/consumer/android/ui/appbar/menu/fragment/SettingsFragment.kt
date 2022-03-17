package com.pieaksoft.event.consumer.android.ui.appbar.menu.fragment

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentSettingsBinding
import com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter.SettingsAdapter
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment


class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private val settingData: List<String> by lazy {
        listOf(
            "Signature"
        )
    }

    private val settingsAdapter: SettingsAdapter by lazy { SettingsAdapter { v -> onClickItem(v) } }

    init {
        requiresBottomNavigation = false
    }

    override fun setupView() {
        settingsAdapter.list = settingData
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
            adapter = settingsAdapter
        }
    }

    private fun onClickItem(itemName: String) {
//        when (itemName) {
//            "Signature" -> startActivity(SignatureActivity.newInstance(requireContext()))
//        }
    }
}