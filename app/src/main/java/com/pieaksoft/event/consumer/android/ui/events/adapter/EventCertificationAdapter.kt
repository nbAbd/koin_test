package com.pieaksoft.event.consumer.android.ui.events.adapter

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.ui.base.BaseAdapter
import com.pieaksoft.event.consumer.android.utils.switchSelectStopIcon

class EventCertificationAdapter : BaseAdapter<String>(R.layout.item_cert) {

    val dateList = mutableListOf<String>()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, item: String) {
        holder.itemView.findViewById<AppCompatTextView>(R.id.cert_date).text = item
        holder.itemView.findViewById<AppCompatImageView>(R.id.radio_btn).switchSelectStopIcon(dateList.contains(item))

        holder.itemView.setOnClickListener {
            if(dateList.contains(item)){
                dateList.remove(item)
            } else {
                dateList.add(item)
            }
            notifyDataSetChanged()
            onClick(position, item)
        }
    }
}