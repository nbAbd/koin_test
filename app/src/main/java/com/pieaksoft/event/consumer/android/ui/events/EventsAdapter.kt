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
import com.pieaksoft.event.consumer.android.utils.getCode
import com.pieaksoft.event.consumer.android.views.gant.MyGanttAdapter


class EventsAdapter : RecyclerView.Adapter<EventsAdapter.EventsAdapterViewHolder>() {

    var list: Map<String, List<Event>> = emptyMap()


    inner class EventsAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(eventList: List<Event>, position: Int) {
            val fullList: MutableList<MyGantItem> = ArrayList()
            val offEvent = eventList.firstOrNull { it.getCode() == "Off" }
            val SB = eventList.firstOrNull { it.getCode() == "SB" }
            val D = eventList.firstOrNull { it.getCode() == "D" }
            val On = eventList.firstOrNull { it.getCode() == "On" }


            if (offEvent != null) {
                val startPoint = offEvent.time?.split(":")?.toTypedArray()?.first()?.toInt() ?: 0
                val endPoint =  startPoint+5
                fullList.add(MyGantItem(false, "Off", Point(startPoint, endPoint)))
            } else {
                fullList.add(MyGantItem(false, "Off", Point(0, 0)))
            }
            if (SB != null) {
                val startPoint = SB.time?.split(":")?.toTypedArray()?.first()?.toInt() ?: 0
                val endPoint =  startPoint+5
                fullList.add(
                    MyGantItem(
                        false,
                        "SB",
                        Point(startPoint, endPoint)
                    )
                )
            } else {
                fullList.add(MyGantItem(false, "SB", Point(0, 0)))
            }
            if (D != null) {
                val startPoint = D.time?.split(":")?.toTypedArray()?.first()?.toInt() ?: 0
                val endPoint =  startPoint+5
                fullList.add(MyGantItem(false, "D", Point(startPoint, endPoint)))
            } else {
                fullList.add(MyGantItem(false, "D", Point(0, 0)))
            }
            if (On != null) {
                val startPoint = On.time?.split(":")?.toTypedArray()?.first()?.toInt() ?: 0
                val endPoint =  startPoint+5
                fullList.add(MyGantItem(false, "On", Point(startPoint, endPoint)))
            } else {
                fullList.add(MyGantItem(false, "On", Point(0, 0)))
            }


            val adapter = MyGanttAdapter(itemView.context, fullList)
            val body = getBody(fullList)

            adapter.setFirstBody(body)
            adapter.header = header
            adapter.body = body
            adapter.setSection(body)
            itemView.findViewById<TableFixHeaders>(R.id.tablefixheaders).adapter = adapter
        }
    }

    override fun onBindViewHolder(holder: EventsAdapterViewHolder, position: Int) {
        val eventGroup = list[list.keys.elementAt(position)]
        eventGroup?.let { holder.bind(it, position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsAdapterViewHolder {
        return EventsAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        )
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