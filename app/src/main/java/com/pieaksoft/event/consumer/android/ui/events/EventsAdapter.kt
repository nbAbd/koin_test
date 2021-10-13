package com.pieaksoft.event.consumer.android.ui.events

import android.graphics.Point
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inqbarna.tablefixheaders.TableFixHeaders
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.MyGantItem
import com.pieaksoft.event.consumer.android.utils.Common
import com.pieaksoft.event.consumer.android.views.gant.MyGanttAdapter


class EventsAdapter: RecyclerView.Adapter<EventsAdapter.EventsAdapterViewHolder>() {

    var list: Map<String, List<Event>> = emptyMap()


    inner class EventsAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(eventList: List<Event>, position: Int) {
            val fullList: MutableList<MyGantItem> = ArrayList()
            val row1 = MyGantItem(false, "Off", Point(0, 3))
            val row2 = MyGantItem(false, "SB", Point(4, 6))
            val row3 = MyGantItem(false, "D", Point(4, 6))
            val row4 = MyGantItem(false, "On", Point(7, 10))

            fullList.add(row1)
            fullList.add(row2)
            fullList.add(row3)
            fullList.add(row4)

            val adapter = MyGanttAdapter(itemView.context, fullList)
            val body = getBody(fullList)

            adapter.setFirstBody(body)
            adapter.header = header
            adapter.body = body
            adapter.setSection(body)
            itemView.findViewById<TableFixHeaders>(R.id.tablefixheaders).adapter =  adapter
            Log.e("test_log","test = onBind" )
        }
    }

    override fun onBindViewHolder(holder: EventsAdapterViewHolder, position: Int) {
        val eventGroup = list[list.keys.elementAt(position)]
        eventGroup?.let { holder.bind(it, position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsAdapterViewHolder {
        return EventsAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false))
    }

    override fun getItemCount(): Int {
      return list.size
    }

    private val header: MutableList<String>
        private get() {
            val header: MutableList<String> = ArrayList()
            for (i in 0 until Common.HEADER_COUNT) header.add(StringBuilder().append(i).toString())
            return header
        }

    private fun getBody(fullList: MutableList<MyGantItem>): MutableList<List<String>> {
        val rows: MutableList<List<String>> = ArrayList()

        for (ganttItem in fullList) {
            val cols: MutableList<String> = ArrayList()
            if (!ganttItem.isEmpty) {
                for (col in 0 until Common.COLUMN_COUNT)
                    if (col >= ganttItem.point.x && col < ganttItem.point.y)
                        if (ganttItem.isError) cols.add("error") else cols.add(ganttItem.title)
                    else cols.add("empty")
                rows.add(cols)
            } else {
                for (col in 0 until Common.COLUMN_COUNT) cols.add("empty")
                rows.add(cols)
            }
        }
        return rows
    }
}