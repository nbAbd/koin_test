package com.pieaksoft.event.consumer.android.ui.events.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pieaksoft.event.consumer.android.ui.events.InsertEventFirstFragment
import com.pieaksoft.event.consumer.android.ui.events.InsertEventListener
import com.pieaksoft.event.consumer.android.ui.events.InsertEventSecondFragment

class InsertEventPagerAdapter(fragment: Fragment, private val callback: InsertEventListener) :
    FragmentStateAdapter(fragment) {
    companion object {
        const val NUMBER_OF_PAGES = 2
    }

    override fun getItemCount(): Int = NUMBER_OF_PAGES

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InsertEventFirstFragment.newInstance(callback)
            else -> InsertEventSecondFragment.newInstance(callback)
        }
    }
}