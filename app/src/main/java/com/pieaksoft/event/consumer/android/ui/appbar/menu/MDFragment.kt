package com.pieaksoft.event.consumer.android.ui.appbar.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentMdBinding
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment

class MDFragment : BaseFragment<FragmentMdBinding>() {
    init {
        requiresBottomNavigation = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_md, container, false)
    }

    override fun setupView() {

    }
}