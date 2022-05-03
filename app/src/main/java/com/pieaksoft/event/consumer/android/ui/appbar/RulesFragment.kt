package com.pieaksoft.event.consumer.android.ui.appbar

import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentRulesBinding
import com.pieaksoft.event.consumer.android.model.rules.Rule
import com.pieaksoft.event.consumer.android.model.rules.RulesData
import com.pieaksoft.event.consumer.android.ui.appbar.adapter.RulesPagerAdapter
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment

class RulesFragment : BaseFragment<FragmentRulesBinding>() {

    private val tabTitles: List<String> by lazy {
        listOf(
            binding.root.context.getString(R.string.usa),
            binding.root.context.getString(R.string.canada)
        )
    }

    private val usaRulesList = mutableListOf<RulesData>().apply {
        add(RulesData.Header())
        add(RulesData.Content(Rule("Available driving", "08:00")))
        add(RulesData.Content(Rule("Rest break", "08:00")))
        add(RulesData.Content(Rule("Driving", "08:00")))
        add(RulesData.Content(Rule("On-Duty", "08:00")))
        add(RulesData.Content(Rule("Weekly", "08:00")))
    }

    private val canadaRulesList = mutableListOf<RulesData>().apply {
        add(RulesData.Header())
        add(RulesData.Content(Rule("Available driving", "08:00")))
        add(RulesData.Content(Rule("Rest break", "12:00")))
        add(RulesData.Content(Rule("Driving", "23:00")))
        add(RulesData.Content(Rule("On-Duty", "05:00")))
        add(RulesData.Content(Rule("Weekly", "09:00")))
    }

    override fun setupView() = with(binding) {
        val usaRulesAdapter = RulesPagerAdapter(
            this@RulesFragment, usaRulesList, canadaRulesList
        )
        viewPagerRules.apply {
            adapter = usaRulesAdapter
        }
        val mediator = TabLayoutMediator(tabLayout, viewPagerRules) { tab, position ->
            tab.text = tabTitles[position]
        }
        if (!mediator.isAttached) mediator.attach()

        btnClose.setOnClickListener { findNavController().popBackStack() }
    }
}