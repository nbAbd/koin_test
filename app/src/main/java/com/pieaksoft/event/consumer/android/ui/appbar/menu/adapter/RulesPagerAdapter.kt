package com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pieaksoft.event.consumer.android.model.rules.Rules
import com.pieaksoft.event.consumer.android.model.rules.RulesData
import com.pieaksoft.event.consumer.android.ui.appbar.menu.fragment.RulePageFragment

class RulesPagerAdapter(
    fragment: Fragment,
    private val usaRules: List<RulesData>,
    private val canadaRules: List<RulesData>
) : FragmentStateAdapter(fragment) {
    companion object {
        const val NUMBER_OF_PAGES = 2
    }

    override fun getItemCount(): Int = NUMBER_OF_PAGES

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RulePageFragment(usaRules)
            else -> RulePageFragment(canadaRules)
        }
    }
}