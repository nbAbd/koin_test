package com.pieaksoft.event.consumer.android.ui.events

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.utils.getGantColor
import com.pieaksoft.event.consumer.android.utils.hide
import com.pieaksoft.event.consumer.android.utils.show
import com.pieaksoft.event.consumer.android.utils.toColorInt

class EventCellAdapter : RecyclerView.Adapter<EventCellAdapter.BaseViewHolder>() {

    var fullList: MutableList<String> = ArrayList()

    inner class EventCellAdapterViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun bind(eventList: MutableList<String>, position: Int) {
            if (eventList[position].length > 2) {
                itemView.findViewById<AppCompatTextView>(R.id.text_view).hide()
                itemView.findViewById<View>(R.id.line_body).show()
                if (eventList[position] != "empty") {
                    itemView.findViewById<View>(R.id.line_body)?.setBackgroundResource(R.drawable.gant_line)
                    itemView.findViewById<View>(R.id.line_body)?.setBackgroundColor(eventList[position].toColorInt())
                } else {
                    itemView.findViewById<View>(R.id.line_body)?.setBackgroundResource(R.drawable.dotted)
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
        override fun bind(eventList: MutableList<String>, position: Int) {
            if (eventList[position] == "time") {
                itemView.findViewById<AppCompatTextView>(R.id.title).text = ""
            } else {
                itemView.findViewById<AppCompatTextView>(R.id.title).text = eventList[position]
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
        abstract fun bind(eventList: MutableList<String>, position: Int)
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