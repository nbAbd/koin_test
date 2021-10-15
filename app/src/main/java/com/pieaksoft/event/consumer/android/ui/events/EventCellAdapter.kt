package com.pieaksoft.event.consumer.android.ui.events

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R

class EventCellAdapter: RecyclerView.Adapter<EventCellAdapter.EventCellAdapterViewHolder>()  {

    var fullList: MutableList<String> = ArrayList()


    inner class EventCellAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(eventList: MutableList<String>, position: Int) {
            if(eventList[position] == "time"){
                itemView.findViewById<AppCompatTextView>(R.id.text_view).text = "time"
            } else {
                Log.e("test5_log","test = "+position)
                itemView.findViewById<AppCompatTextView>(R.id.text_view).text = position.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventCellAdapterViewHolder {
        return EventCellAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.text_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EventCellAdapterViewHolder, position: Int) {
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