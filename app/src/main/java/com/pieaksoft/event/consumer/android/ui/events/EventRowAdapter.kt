package com.pieaksoft.event.consumer.android.ui.events

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.MyGantItem

class EventRowAdapter : RecyclerView.Adapter<EventRowAdapter.EventRowAdapterViewHolder>() {

    var fullList: MutableList<MyGantItem> = ArrayList()
    val viewPool = RecyclerView.RecycledViewPool()

    inner class EventRowAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(eventList: List<MyGantItem>, position: Int) {
            val eventCellItems: MutableList<String> = ArrayList()
            if(eventList[position].title == "time"){
                eventCellItems.add("time")
                for (i in 1 .. 25){
                    eventCellItems.add((i-1).toString())
                }
            } else {
              //  val title = eventList[0].title
               // eventCellItems.add(title)
            }

            val adapter = EventCellAdapter()
            itemView.findViewById<RecyclerView>(R.id.gant_rv).adapter = adapter
            itemView.findViewById<RecyclerView>(R.id.gant_rv).layoutManager = LinearLayoutManager(
                itemView.context, LinearLayoutManager.HORIZONTAL, false)
            itemView.findViewById<RecyclerView>(R.id.gant_rv).setRecycledViewPool(viewPool)
            adapter.fullList = eventCellItems
            adapter.notifyDataSetChanged()

            Log.e("test_log4", "test = "+eventCellItems )
            Log.e("test_log44", "test = "+eventList.size )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventRowAdapterViewHolder {
        return EventRowAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EventRowAdapterViewHolder, position: Int) {
        fullList.let { holder.bind(it, position) }
    }

    override fun getItemCount(): Int {
        return fullList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_HEADER_BODY
        } else VIEW_TYPE_EVENT
    }

    companion object {
        const val VIEW_TYPE_HEADER_BODY = 10001
        const val VIEW_TYPE_EVENT = 10002
        const val VIEW_TYPE_EVENT_NAME = 10003
    }
}