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
import com.pieaksoft.event.consumer.android.utils.Storage
import com.pieaksoft.event.consumer.android.utils.getCode
import com.pieaksoft.event.consumer.android.views.gant.MyGanttAdapter
import miguelbcr.ui.tableFixHeadesWrapper.TableFixHeaderAdapter


class EventsAdapter : RecyclerView.Adapter<EventsAdapter.EventsAdapterViewHolder>() {

    var list: Map<String, List<Event>> = emptyMap()


    inner class EventsAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(eventList: List<Event>, position: Int) {
            val fullList: MutableList<MyGantItem> = ArrayList()
            val offEvent = eventList.firstOrNull { it.getCode() == "Off" }
            val SB = eventList.firstOrNull { it.getCode() == "SB" }
            val D = eventList.firstOrNull { it.getCode() == "D" }
            val On = eventList.firstOrNull { it.getCode() == "On" }
            val eventsGroup = eventList.groupBy { it.getCode() ?: "" }


//            eventsGroup.forEach { (key, value) ->
//                value.forEach { event ->
//
//                }
//
//            }


            if (eventsGroup.containsKey("Off")) {
                val points: MutableList<Point> = ArrayList()
                eventsGroup["Off"]?.forEach { event ->
                    val startPoint = event.time?.split(":")?.toTypedArray()?.first()?.toInt() ?: 0
                    val endPoint = startPoint + 5
                    points.add(Point(startPoint, endPoint))
                }
                fullList.add(MyGantItem(false, "Off", points))
            } else {
                fullList.add(MyGantItem(false, "Off", mutableListOf()))
            }

            if (eventsGroup.containsKey("SB")) {
                val points: MutableList<Point> = ArrayList()
                eventsGroup["SB"]?.forEach { event ->
                    val startPoint = event.time?.split(":")?.toTypedArray()?.first()?.toInt() ?: 0
                    val endPoint = startPoint + 5
                    points.add(Point(startPoint, endPoint))
                }
                fullList.add(MyGantItem(false, "SB", points))
            } else {
                fullList.add(MyGantItem(false, "SB", mutableListOf()))
            }

            if (eventsGroup.containsKey("D")) {
                val points: MutableList<Point> = ArrayList()
                eventsGroup["D"]?.forEach { event ->
                    val startPoint = event.time?.split(":")?.toTypedArray()?.first()?.toInt() ?: 0
                    val endPoint = startPoint + 5
                    points.add(Point(startPoint, endPoint))
                }
                fullList.add(MyGantItem(false, "D", points))
            } else {
                fullList.add(MyGantItem(false, "D", mutableListOf()))
            }
            if (eventsGroup.containsKey("On")) {
                val points: MutableList<Point> = ArrayList()
                eventsGroup["On"]?.forEach { event ->
                    val startPoint = event.time?.split(":")?.toTypedArray()?.first()?.toInt() ?: 0
                    val endPoint = startPoint + 5
                    points.add(Point(startPoint, endPoint))
                }
                fullList.add(MyGantItem(false, "On", points))
            } else {
                fullList.add(MyGantItem(false, "On", mutableListOf()))
            }

            Log.e("test_log3", "test = " + fullList)

            val adapter = MyGanttAdapter(itemView.context, fullList)
            val body = getBody(fullList)
            //val body1 = getBody1(fullList)

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

    private fun getFirstBody(fullList: MutableList<MyGantItem>): MutableList<List<String>> {
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

    private fun getBody(fullList: MutableList<MyGantItem>): MutableList<List<String>> {
        val rows: MutableList<List<String>> = ArrayList()

        for (ganttItem in fullList) {
            val cols: MutableList<String> = ArrayList()
            if (!ganttItem.isEmpty) {
                for (col in 0 until Common.COLUMN_COUNT){
                    if (col >= ganttItem.point.x && col < ganttItem.point.y)
                        if (ganttItem.isError) cols.add("error") else cols.add(ganttItem.title)
                    else cols.add("empty")
                    rows.add(cols)
                }
            } else {
                for (col in 0 until Common.COLUMN_COUNT) cols.add("empty")
                rows.add(cols)
            }
        }
        return rows
    }
}