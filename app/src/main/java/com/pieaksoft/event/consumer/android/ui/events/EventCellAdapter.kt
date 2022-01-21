package com.pieaksoft.event.consumer.android.ui.events

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.GantItemPoint
import com.pieaksoft.event.consumer.android.utils.getGantColor
import com.pieaksoft.event.consumer.android.utils.hide
import com.pieaksoft.event.consumer.android.utils.show
import com.pieaksoft.event.consumer.android.utils.toColorInt

class EventCellAdapter : RecyclerView.Adapter<EventCellAdapter.BaseViewHolder>() {

    var fullList: MutableList<GantItemPoint> = ArrayList()

    inner class EventCellAdapterViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun bind(eventList: MutableList<GantItemPoint>, position: Int) {
            val event = eventList[position]
            if (event.title.length > 2) {
                itemView.findViewById<AppCompatTextView>(R.id.text_view).hide()
                itemView.findViewById<View>(R.id.line_body).show()
                if (event.title != "empty") {
                    var startTimeSet = false
                    if (event.pointStart.isNullOrBlank()) {
                        itemView.findViewById<ContentLoadingProgressBar>(R.id.line_body)?.progress =
                            100
                    } else {
                        val minutes =
                            event.pointStart.split(":").toTypedArray().getOrNull(1)?.toInt() ?: 0
                        val lastMin = 60 - minutes
                        val progress = (lastMin.toDouble() / 60) * 100

                        itemView.findViewById<ContentLoadingProgressBar>(R.id.line_body)?.progress =
                            progress.toInt()
                        itemView.findViewById<ContentLoadingProgressBar>(R.id.line_body)?.rotation =
                            180f
                        startTimeSet = true
                    }
                    if (!startTimeSet) {
                        if (event.pointEnd.isNullOrBlank()) {
                            itemView.findViewById<ContentLoadingProgressBar>(R.id.line_body)?.progress =
                                100
                        } else {
                            val minutes =
                                event.pointEnd.split(":").toTypedArray().getOrNull(1)?.toInt()
                                    ?: 0
                            val progress = (minutes.toDouble() / 60) * 100
                            itemView.findViewById<ContentLoadingProgressBar>(R.id.line_body)?.progress =
                                progress.toInt()
                        }
                    }

                    itemView.findViewById<ContentLoadingProgressBar>(R.id.line_body)?.progressTintList =
                        ColorStateList.valueOf(event.title.toColorInt())
                } else {
                    itemView.findViewById<ContentLoadingProgressBar>(R.id.line_body)?.progress = 0
                }
            } else {
                itemView.findViewById<AppCompatTextView>(R.id.text_view).show()
                itemView.findViewById<View>(R.id.line_body).hide()
                if (position == 1 || position == 25) {
                    itemView.findViewById<AppCompatTextView>(R.id.text_view).text = "M"
                } else {
                    itemView.findViewById<AppCompatTextView>(R.id.text_view).text =
                        (position - 1).toString()
                }
            }
        }
    }

    inner class EventFirstCellAdapterViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun bind(eventList: MutableList<GantItemPoint>, position: Int) {
            if (eventList[position].title == "time") {
                itemView.findViewById<AppCompatTextView>(R.id.title).text = ""
            } else {
                itemView.findViewById<AppCompatTextView>(R.id.title).text =
                    eventList[position].title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == VIEW_TYPE_FIRST_BODY) {
            EventFirstCellAdapterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_gant_first_cell, parent, false)
            )
        } else {
            EventCellAdapterViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.text_view, parent, false)
            )
        }
    }

    abstract inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(eventList: MutableList<GantItemPoint>, position: Int)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        fullList.let { holder.bind(it, position) }
    }

    override fun getItemCount(): Int {
        return fullList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_FIRST_BODY
        } else VIEW_TYPE_EVENT_CELL
    }

    companion object {
        const val VIEW_TYPE_FIRST_BODY = 10001
        const val VIEW_TYPE_EVENT_CELL = 10002
    }
}