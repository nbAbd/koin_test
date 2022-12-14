package com.pieaksoft.event.consumer.android.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>(
    @LayoutRes val layoutId: Int,
    private var listener: ItemClickListener<T>? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: ArrayList<T> = arrayListOf()

    fun update(list: ArrayList<T>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BaseHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        holder.itemView.setOnClickListener {
            onClick(position, item)
        }
        onBindViewHolder(holder, position, item)
    }

    abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, item: T)

    class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setClickListener(listener: ItemClickListener<T>) {
        this.listener = listener
    }

    interface ItemClickListener<T> {
        fun onClick(position: Int, item: T)
    }

    open fun onClick(position: Int, item: T) {
        listener?.onClick(position, item)
    }
}