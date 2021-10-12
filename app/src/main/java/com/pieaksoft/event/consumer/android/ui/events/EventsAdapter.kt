package com.pieaksoft.event.consumer.android.ui.events

import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.ui.base.BaseAdapter


class EventsAdapter: BaseAdapter<Event>(R.layout.item_event) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, item: Event) {

    }
}